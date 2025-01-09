package york.pharmacy.kafka;

import jakarta.validation.constraints.NotNull;

public class ConsumerEvent {

    private String eventType;
    private String prescriptionId;
    private String patientId;
    private String medicineCode;
    private int quantity;
    private String instructions;

    public ConsumerEvent() {}

    public ConsumerEvent(String eventType, String prescriptionId) {
        this.eventType = eventType;
        this.prescriptionId = prescriptionId;
    }

    public ConsumerEvent(String eventType, String prescriptionId, String patientId, String medicineCode, int quantity, String instructions) {
        this.eventType = eventType;
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.medicineCode = medicineCode;
        this.quantity = quantity;
        this.instructions = instructions;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
