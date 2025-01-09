package york.pharmacy.kafka;

import java.time.LocalDate;

public class ProducerEvent {

    private String eventType;
    private String prescriptionId;
    private LocalDate deliveryDate;

    public ProducerEvent() {}

    public ProducerEvent(String eventType, String prescriptionId) {
        this.eventType = eventType;
        this.prescriptionId = prescriptionId;
    }

    public ProducerEvent(String eventType, String prescriptionId, LocalDate deliveryDate) {
        this.eventType = eventType;
        this.prescriptionId = prescriptionId;
        this.deliveryDate = deliveryDate;
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

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Override
    public String toString() {
        return "ProducerEvent [eventType: " + eventType + ", prescriptionId: " + prescriptionId + ", deliveryDate: " + deliveryDate + "]";
    }
}
