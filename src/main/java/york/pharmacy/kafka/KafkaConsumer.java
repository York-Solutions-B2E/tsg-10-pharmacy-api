package york.pharmacy.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import york.pharmacy.prescriptions.PrescriptionService;
import york.pharmacy.prescriptions.dto.PrescriptionRequest;
import york.pharmacy.utilities.ServiceUtility;

@Service
public class KafkaConsumer {


    private final KafkaTemplate<String, ConsumerEvent> kafkaTemplate;
    private final ServiceUtility serviceUtility;

    public KafkaConsumer(KafkaTemplate<String, ConsumerEvent> kafkaTemplate, ServiceUtility serviceUtility) {
        this.kafkaTemplate = kafkaTemplate;
        this.serviceUtility = serviceUtility;
    }

    public void sendMessage(String topic, ConsumerEvent e) {
        kafkaTemplate.send(topic, e);
//        System.out.println("Message sent: " + e);
    }


//    @KafkaListener(topics = "test-topic-receiving", groupId = "pharmacy-group")
    @KafkaListener(topics = "patient_prescription_events", groupId = "pharmacy-group")
    public void listen(ConsumerRecord<String, ConsumerEvent> record) {
        ConsumerEvent event = record.value();
        System.out.println("Received Event: " + event.getEventType() + " - " + event.getPrescriptionId());
        if (event.getEventType().equals("NEW_PRESCRIPTION")) {
            PrescriptionRequest request = new PrescriptionRequest();
            // We originally accidentally set this to the prescriptionId
            request.setPatientId(event.getPatientId());
            request.setPrescriptionNumber(event.getPrescriptionId());
            request.setMedicineCode(event.getMedicineCode());
            request.setQuantity(event.getQuantity());
            request.setInstructions(event.getInstructions());

            serviceUtility.addPrescription(request);
        } else if (event.getEventType().equals("CANCELLED")) {
            serviceUtility.cancelPrescription(event.getPrescriptionId());
        }


    }
}
