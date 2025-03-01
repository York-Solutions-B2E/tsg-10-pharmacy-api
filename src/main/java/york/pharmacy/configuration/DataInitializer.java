package york.pharmacy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import york.pharmacy.inventory.InventoryService;
import york.pharmacy.inventory.dto.InventoryRequest;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.MedicineService;
import york.pharmacy.orders.OrderService;
import york.pharmacy.orders.dto.OrderRequest;
import york.pharmacy.orders.OrderStatus;
import york.pharmacy.prescriptions.PrescriptionService;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MedicineService medicineService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final PrescriptionService prescriptionService;

    @Autowired
    public DataInitializer(MedicineService medicineService, InventoryService inventoryService, OrderService orderService, PrescriptionService prescriptionService) {
        this.medicineService = medicineService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.prescriptionService = prescriptionService;
    }

    @Override
    public void run(String... args) {
        seedMedicines();
        seedInventories();
        seedPrescriptions();
        seedOrders();
    }

    private void seedMedicines() {
        List<MedicineRequest> medicineRequests = List.of(
                new MedicineRequest("ChocoRelief", "CRX-001"),
                new MedicineRequest("MintyCure", "MCX-002"),
                new MedicineRequest("Caramelex", "CEX-003"),
                new MedicineRequest("GummyVita", "GVX-004"),
                new MedicineRequest("Lollipoprin", "LPX-005"),
                new MedicineRequest("CandyCaps", "CCX-006"),
                new MedicineRequest("JellyGel", "JGX-007"),
                new MedicineRequest("SourSooth", "SSX-008"),
                new MedicineRequest("FizzFluAid", "FFX-009"),
                new MedicineRequest("NougatNite", "NNX-010"),
                new MedicineRequest("ToffeeTabs", "TTX-011"),
                new MedicineRequest("PecanPlenty", "PPX-012"),
                new MedicineRequest("SprinkleSymptomRelief", "SRX-013"),
                new MedicineRequest("BerryBoost", "BBX-014"),
                new MedicineRequest("CottonCloud", "CTC-015"),
                new MedicineRequest("TwistRelieve", "TWX-016"),
                new MedicineRequest("CocoaCalm", "CCX-017"),
                new MedicineRequest("SugarSoothe", "SSG-018"),
                new MedicineRequest("MallowMed", "MMX-019"),
                new MedicineRequest("FudgeFlex", "FFX-020"),
                new MedicineRequest("CrunchyCapsule", "CCX-021"),
                new MedicineRequest("SherbetEase", "SEX-022"),
                new MedicineRequest("PopRockPellets", "PRX-023"),
                new MedicineRequest("DoughnutDream", "DDX-024"),
                new MedicineRequest("BubbleRelief", "BRX-025"),
                new MedicineRequest("ChurroChew", "CCW-026"),
                new MedicineRequest("IceCreamEase", "ICR-027"),
                new MedicineRequest("CookieCure", "CKX-028"),
                new MedicineRequest("MacaronMax", "MMX-029"),
                new MedicineRequest("CandyCanestril", "CCX-030")
        );

        System.out.println("Seeding Medicines...");
        medicineService.batchCreateMedicines(medicineRequests);
        System.out.println("Medicines Seeded.");
    }

    private void seedInventories() {
        System.out.println("Seeding Inventories...");
        List<InventoryRequest> inventoryRequests = List.of(
                new InventoryRequest(1L, 100),
                new InventoryRequest(2L, 200),
                new InventoryRequest(3L, 150),
                new InventoryRequest(4L, 180),
                new InventoryRequest(5L, 90),
                new InventoryRequest(6L, 120),
                new InventoryRequest(7L, 140),
                new InventoryRequest(8L, 160),
                new InventoryRequest(9L, 110),
                new InventoryRequest(10L, 130),
                new InventoryRequest(11L, 170),
                new InventoryRequest(12L, 190),
                new InventoryRequest(13L, 100),
                new InventoryRequest(14L, 105),
                new InventoryRequest(15L, 115),
                new InventoryRequest(16L, 125),
                new InventoryRequest(17L, 135),
                new InventoryRequest(18L, 145),
                new InventoryRequest(19L, 155),
                new InventoryRequest(20L, 165),
                new InventoryRequest(21L, 175),
                new InventoryRequest(22L, 185),
                new InventoryRequest(23L, 195),
                new InventoryRequest(24L, 205),
                new InventoryRequest(25L, 215),
                new InventoryRequest(26L, 225),
                new InventoryRequest(27L, 235),
                new InventoryRequest(28L, 245),
                new InventoryRequest(29L, 255),
                new InventoryRequest(30L, 265)
        );

        inventoryService.createManyInventories(inventoryRequests);
        System.out.println("Inventories Seeded.");
    }

    private void seedPrescriptions() {
        System.out.println("Seeding Prescriptions...");
        List<PrescriptionRequest> prescriptionRequests = List.of(
                new PrescriptionRequest(generateRandomPatientId(), "CRX-001", generateRandomPrescriptionNumber(), 200, "Take twice daily"),
                new PrescriptionRequest(generateRandomPatientId(), "MCX-002", generateRandomPrescriptionNumber(), 200, "Take before meals"),
                new PrescriptionRequest(generateRandomPatientId(), "CEX-003", generateRandomPrescriptionNumber(), 200, "Take with water"),
                new PrescriptionRequest(generateRandomPatientId(), "GVX-004", generateRandomPrescriptionNumber(), 200, "Take in the morning"),
                new PrescriptionRequest(generateRandomPatientId(), "LPX-005", generateRandomPrescriptionNumber(), 200, "Take at bedtime"),
                new PrescriptionRequest(generateRandomPatientId(), "CCX-006", generateRandomPrescriptionNumber(), 200, "Take after meals"),
                new PrescriptionRequest(generateRandomPatientId(), "JGX-007", generateRandomPrescriptionNumber(), 200, "Take every 6 hours"),
                new PrescriptionRequest(generateRandomPatientId(), "SSX-008", generateRandomPrescriptionNumber(), 200, "Take with food"),
                new PrescriptionRequest(generateRandomPatientId(), "FFX-009", generateRandomPrescriptionNumber(), 200, "Take once daily"),
                new PrescriptionRequest(generateRandomPatientId(), "NNX-010", generateRandomPrescriptionNumber(), 200, "Take as needed")
        );

        prescriptionRequests.forEach(prescriptionService::addPrescription);
        System.out.println("Prescriptions Seeded.");
    }

    private String generateRandomPatientId() {
        return "PID-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // Generates a random string like "PID-1A2B3C4D"
    }

    private String generateRandomPrescriptionNumber() {
        return "RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        // Generates a random string like "RX-5E6F7G8H"
    }
    private void seedOrders() {
        System.out.println("Seeding Orders...");
        List<OrderRequest> orderRequests = List.of(
                new OrderRequest(1L, 100, LocalDate.now().plusDays(5)),
                new OrderRequest(2L, 200, LocalDate.now().plusDays(10)),
                new OrderRequest(3L, 300, LocalDate.now().plusDays(7)),
                new OrderRequest(4L, 100, LocalDate.now().plusDays(3)),
                new OrderRequest(5L, 200, LocalDate.now().plusDays(20))
        );

        orderService.batchCreateOrders(orderRequests);
        System.out.println("Orders Seeded.");
    }

}
