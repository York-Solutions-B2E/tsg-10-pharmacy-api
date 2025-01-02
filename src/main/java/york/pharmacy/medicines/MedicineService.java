package york.pharmacy.medicines;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.dto.MedicineResponse;
import york.pharmacy.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    // Create a single medicine
    public MedicineResponse createMedicine(MedicineRequest medicineRequest) {
        Medicine medicine = MedicineMapper.toEntity(medicineRequest);
        Medicine savedMedicine = medicineRepository.save(medicine);
        return MedicineMapper.toResponse(savedMedicine);
    }

    // Create a batch of medicines
    public List<MedicineResponse> batchCreateMedicines(List<MedicineRequest> medicineRequests) {
        List<Medicine> medicines = medicineRequests.stream()
                .map(MedicineMapper::toEntity)
                .collect(Collectors.toList());
        List<Medicine> savedMedicines = medicineRepository.saveAll(medicines);
        return savedMedicines.stream()
                .map(MedicineMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get all medicines
    public List<MedicineResponse> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(MedicineMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get a medicine by ID
    public MedicineResponse getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine with ID " + id + " not found"));
        return MedicineMapper.toResponse(medicine);
    }

    // Update a medicine by ID
    public MedicineResponse updateMedicine(Long id, MedicineRequest medicineRequest) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine with ID " + id + " not found"));

        medicine.setName(medicineRequest.getName());
        medicine.setCode(medicineRequest.getCode());
        Medicine updatedMedicine = medicineRepository.save(medicine);
        return MedicineMapper.toResponse(updatedMedicine);
    }

    // Delete a medicine by ID
    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine with ID " + id + " not found");
        }
        medicineRepository.deleteById(id);
    }

    public Medicine getMedicineByCode(String code) {
        Optional<Medicine> medicineOptional = Optional.ofNullable(medicineRepository.findMedicineByCode(code));
        return medicineOptional.
                orElseThrow(() -> new ResourceNotFoundException("Medicine with Code " + code + " not found"));
    }
}
