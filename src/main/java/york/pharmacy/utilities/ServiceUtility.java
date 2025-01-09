package york.pharmacy.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.exceptions.ResourceNotFoundException;
import york.pharmacy.inventory.Inventory;
import york.pharmacy.inventory.InventoryMapper;
import york.pharmacy.inventory.InventoryRepository;
import york.pharmacy.inventory.dto.InventoryResponse;
import york.pharmacy.kafka.KafkaProducer;
import york.pharmacy.medicines.Medicine;
import york.pharmacy.medicines.MedicineRepository;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.kafka.ProducerEvent;
import york.pharmacy.orders.Order;
import york.pharmacy.orders.OrderRepository;
import york.pharmacy.prescriptions.Prescription;
import york.pharmacy.prescriptions.PrescriptionMapper;
import york.pharmacy.prescriptions.PrescriptionRepository;
import york.pharmacy.prescriptions.PrescriptionStatus;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceUtility {

    private final MedicineRepository medicineRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final KafkaProducer kafkaProducer;

    //------------------------------------------------------------------------------------------------------------------//
    // Medicine methods
    //------------------------------------------------------------------------------------------------------------------//

    public Medicine getMedicineByCode(String code) {
        Optional<Medicine> medicineOptional = Optional.ofNullable(medicineRepository.findMedicineByCode(code));
        return medicineOptional.
                orElseThrow(() -> new ResourceNotFoundException("Medicine with Code " + code + " not found"));
    }

    // Helper Method - fetch a Medicine entity by ID
    public Medicine fetchMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine with ID " + id + " not found"));
    }

    //------------------------------------------------------------------------------------------------------------------//
    // Inventory methods
    //------------------------------------------------------------------------------------------------------------------//

    public InventoryResponse adjustStockQuantity(Long id, Integer pillAdjustment) {
        Inventory existingEntity = inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory not found with id: " + id));

        int newQuantity = existingEntity.getStockQuantity() + pillAdjustment;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below 0");
        }
        existingEntity.setStockQuantity(newQuantity);

        Inventory updatedEntity = inventoryRepository.save(existingEntity);
        return InventoryMapper.toResponse(updatedEntity);
    }

    // Helper method - Used in service layer to fetch Inventory entity by ID
    public Inventory fetchInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory with ID " + id + " not found"));
    }

    //------------------------------------------------------------------------------------------------------------------//
    // Order methods
    //------------------------------------------------------------------------------------------------------------------//

    // Helper Method - Return the closest upcoming delivery date for a specific inventory id (Filtered by "ORDERED" Status)
    public Optional<Order> getClosestOrderedDeliveryDate(Long inventoryId) {
        return orderRepository.findFirstOrderByInventoryIdAndStatusOrderedAndFutureDeliveryDate(
                LocalDate.now(),
                inventoryId
        );
    }

    //------------------------------------------------------------------------------------------------------------------//
    // Prescription methods
    //------------------------------------------------------------------------------------------------------------------//

    public PrescriptionResponse addPrescription(PrescriptionRequest prescriptionRequest) {
        Medicine medicine = this.getMedicineByCode(prescriptionRequest.getMedicineCode()); // need to add this method
        Prescription prescription = PrescriptionMapper.toEntity(prescriptionRequest, medicine);
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        Long medicineId = medicine.getId();
        Long prescriptionId = savedPrescription.getId();
        // Call updatePrescriptionsWithNewStock here, and add to prescriptionService as well
        if (!checkAndUpdatePrescriptionStock(medicineId, prescriptionId)) {
            savedPrescription.setStatus(PrescriptionStatus.OUT_OF_STOCK);
            prescriptionRepository.save(savedPrescription);
        }
//        updateInventoryStockStatus(medicine.getId());
        // Kafka publish RECEIVED
        ProducerEvent event = new ProducerEvent(
                "RECEIVED",
                savedPrescription.getPrescriptionNumber()
        );
        kafkaProducer.sendMessage("prescription_status_updates", event);

        return PrescriptionMapper.toResponse(savedPrescription);
    }

    // cancel prescription
    // changed the id to prescriptionNumber since that's what's coming from Kafka
    public void cancelPrescription(String prescriptionNumber) {
//        Prescription prescription = prescriptionRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Prescription with id" + id + " not found") );
        Prescription prescription = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber).
                orElseThrow(() -> new ResourceNotFoundException("Prescription with prescription number " + prescriptionNumber + " not found"));

        prescription.setStatus(PrescriptionStatus.CANCELLED);
        prescriptionRepository.save(prescription);
    }

    public List<Prescription> updateAwaitingShipmentStatus(Order order) {
        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK);
        List<Prescription> prescriptions = prescriptionRepository.findAllByMedicineIdAndStatus(order.getInventory().getMedicine().getId(), statuses);
        for (Prescription p : prescriptions) {
            p.setOrder(order);
            p.setStatus(PrescriptionStatus.AWAITING_SHIPMENT);
            //publish to kafka BACK_ORDERED, p.getPrescriptionNumber(), order.getDate()
            Prescription savedPrescription = prescriptionRepository.save(p);
            ProducerEvent event = new ProducerEvent(
                    "BACK_ORDERED",
                    p.getPrescriptionNumber(),
                    order.getDeliveryDate()
            );
            kafkaProducer.sendMessage("prescription_status_updates", event);
        }

        return prescriptions;
    }

    public List<Prescription> updateStockReceivedStatus(Order order) {
        List<Prescription> prescriptions = prescriptionRepository.findAllByOrder(order);
        System.out.println("Prescription Status Received List:" + prescriptions.size());

        for (Prescription p : prescriptions) {
            p.setStatus(PrescriptionStatus.STOCK_RECEIVED);
            Prescription savedPrescription = prescriptionRepository.save(p);
        }
//        updateInventoryStockStatus(order.getInventory().getMedicine().getId());

        return prescriptions;
    }

    // need a helper function to return the needed count for new orders
    public int minOrderCount(Long medicineId) {
        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK);

        return prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses);
    }

    //  ----------
    //  cross-entity method
    //  -----------

    /**
     * Checks if there is sufficient inventory for prescriptions and updates status accordingly
     * @param medicineId The ID of the medicine to check
     * @param prescriptionId The ID of the new prescription to check
     * @return true if sufficient stock available, false if insufficient
     */
    public boolean checkAndUpdatePrescriptionStock(Long medicineId, Long prescriptionId) {
        // Get relevant prescription statuses
        List<PrescriptionStatus> statuses = List.of(
                PrescriptionStatus.NEW,
                PrescriptionStatus.OUT_OF_STOCK,
                PrescriptionStatus.STOCK_RECEIVED
        );

        // Get total quantity needed for all relevant prescriptions
        int totalQuantityNeeded = prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(
                medicineId,
                statuses
        );

        // Get current inventory
        Inventory inventory = inventoryRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for medicine ID: " + medicineId));

        int currentStock = inventory.getStockQuantity();

        // If stock is insufficient, mark only the new prescription as OUT_OF_STOCK
        if (currentStock < totalQuantityNeeded) {
            Prescription newPrescription = prescriptionRepository.findById(prescriptionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + prescriptionId));
            return false;
        }

        return true;
    }

    /**
     * Update the inventory's stock quantity to the given value, then update
     * all prescriptions (that are currently in {@link PrescriptionStatus#NEW} or
     * {@link PrescriptionStatus#OUT_OF_STOCK}) to {@link PrescriptionStatus#STOCK_RECEIVED}
     * as long as there is enough stock to fulfill them. Decrement the stock
     * quantity as you go. Break out when you're out of stock.
     *
     * @param newStockQuantity an updated total number of pills in the inventory.
     * @param medicineId       the primary key (ID) of the Medicine table.
     */
    public void updatePrescriptionsWithNewStock(int newStockQuantity, Long medicineId) {

        // Are these the right statuses?
        List<PrescriptionStatus> statusesToUpdate = List.of(
                PrescriptionStatus.NEW,
                PrescriptionStatus.OUT_OF_STOCK,
                PrescriptionStatus.STOCK_RECEIVED
        );
        List<Prescription> pendingPrescriptions =
                prescriptionRepository.findAllByMedicineIdAndStatus(medicineId, statusesToUpdate);

        // Decrement from newStockQuantity as we fulfill prescriptions
        int currentStock = newStockQuantity;
        for (Prescription p : pendingPrescriptions) {
            int pillsNeeded = p.getQuantity();

            // If no more stock is left, break early
            if (currentStock <= 0) {
                break;
            }

            if (pillsNeeded <= currentStock) {

                if (p.getStatus() == PrescriptionStatus.OUT_OF_STOCK) {
                    p.setStatus(PrescriptionStatus.STOCK_RECEIVED);
                    prescriptionRepository.save(p);
                }

                // Decrement the "currentStock"
                currentStock -= pillsNeeded;

                Long inventoryId = inventoryRepository.findByMedicineId(medicineId).orElseThrow(
                        () -> new ResourceNotFoundException("No inventory for given medicine")
                ).getId();

            } else {
                // If we can't fill it completely, mark it out_of_stock
                p.setStatus(PrescriptionStatus.OUT_OF_STOCK);
            }
        }
    }


    //    // send med id, total count of active prescriptions for that medicine
    // include STOCK_RECEIVED
//    public void updateInventoryStockStatus(Long medicineId) {
//        List<PrescriptionStatus> statuses = List.of(PrescriptionStatus.NEW, PrescriptionStatus.OUT_OF_STOCK, PrescriptionStatus.STOCK_RECEIVED);
//        HashMap<Long, Integer> medicineCount = new HashMap<>();
//        int totalCount = prescriptionRepository.findTotalQuantityByMedicineIdAndStatus(medicineId, statuses);
//
//        medicineCount.put(medicineId, totalCount);
//        this.updateSufficientStock(medicineCount);
//    }

    //------------------------------------------------------------------------------------------------------------------//
    // Kafka methods
    //------------------------------------------------------------------------------------------------------------------//

    public void publishPickedUpOrFilled(String eventType ,String prescriptionNumber) {
        ProducerEvent event = new ProducerEvent(
                eventType,
                prescriptionNumber
        );
        kafkaProducer.sendMessage("prescription_status_updates", event);
    }
}