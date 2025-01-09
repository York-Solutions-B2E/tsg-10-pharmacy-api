package york.pharmacy.prescriptions;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.prescriptions.dto.PrescriptionResponse;
import york.pharmacy.prescriptions.dto.PrescriptionStatusRequest;

import york.pharmacy.utilities.ServiceUtility;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final ServiceUtility serviceUtility;

    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(@Valid @RequestBody PrescriptionRequest prescriptionRequest) {
        PrescriptionResponse prescriptionResponse = prescriptionService.addPrescription(prescriptionRequest);

        // Get the medicine ID from the prescription response
        Long medicineId = prescriptionResponse.getMedicine().getId();

        // Check and update stock status
        serviceUtility.checkAndUpdatePrescriptionStock(medicineId, prescriptionResponse.getId());

        return new ResponseEntity<>(prescriptionResponse, HttpStatus.CREATED);
    }

    // get all prescriptions
    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> getAllPrescriptions() {
        List<PrescriptionResponse> prescriptionResponses = prescriptionService.getAllPrescriptions();
        return new ResponseEntity<>(prescriptionResponses, HttpStatus.OK);
    }

    // get all active prescriptions
    @GetMapping("/active")
    public ResponseEntity<List<PrescriptionResponse>> getAllActivePrescriptions() {
        List<PrescriptionResponse> prescriptionResponses = prescriptionService.getActivePrescriptions();
        return new ResponseEntity<>(prescriptionResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable Long id) {
        PrescriptionResponse prescriptionResponse = prescriptionService.getPrescriptionById(id);
        return new ResponseEntity<>(prescriptionResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> updatePrescription(@PathVariable Long id, @Valid @RequestBody PrescriptionStatusRequest prescriptionStatusRequest) {
        PrescriptionResponse prescriptionResponse = prescriptionService.updatePrescription(id, prescriptionStatusRequest);
        return new ResponseEntity<>(prescriptionResponse, HttpStatus.OK);
    }


}
