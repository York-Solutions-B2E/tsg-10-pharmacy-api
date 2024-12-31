package york.pharmacy.medicines;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import york.pharmacy.medicines.dto.MedicineRequest;
import york.pharmacy.medicines.dto.MedicineResponse;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    // Create a new medicine
    @PostMapping
    public ResponseEntity<MedicineResponse> createMedicine(@Valid @RequestBody MedicineRequest medicineRequest) {
        MedicineResponse medicineResponse = medicineService.createMedicine(medicineRequest);
        return new ResponseEntity<>(medicineResponse, HttpStatus.CREATED);
    }

    // Create multiple medicines (batch)
    @PostMapping("/batch")
    public ResponseEntity<List<MedicineResponse>> batchCreateMedicines(@Valid @RequestBody List<MedicineRequest> medicineRequests) {
        List<MedicineResponse> medicineResponses = medicineService.batchCreateMedicines(medicineRequests);
        return new ResponseEntity<>(medicineResponses, HttpStatus.CREATED);
    }

    // Get all medicines
    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {
        List<MedicineResponse> medicines = medicineService.getAllMedicines();
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    // Get a medicine by its ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable Long id) {
        MedicineResponse medicineResponse = medicineService.getMedicineById(id);
        return new ResponseEntity<>(medicineResponse, HttpStatus.OK);
    }

    // Update an existing medicine by its ID
    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(@PathVariable Long id, @Valid @RequestBody MedicineRequest medicineRequest) {
        MedicineResponse medicineResponse = medicineService.updateMedicine(id, medicineRequest);
        return new ResponseEntity<>(medicineResponse, HttpStatus.OK);
    }

    // Delete a medicine by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
