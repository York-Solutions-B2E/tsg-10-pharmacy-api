package york.pharmacy.kafka;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaProducer producerService;
    private final KafkaConsumer consumerService;
    public KafkaController(final KafkaProducer producerService, final KafkaConsumer consumerService) {
        this.producerService = producerService;
        this.consumerService = consumerService;
    }

    @PostMapping("/send")
    public ResponseEntity<ProducerEvent> sendMessage(@RequestBody ProducerEvent e) {
        producerService.sendMessage("test-topica", e);
        return ResponseEntity.ok(e);
    }

    @PostMapping("/receive")
    public ResponseEntity<ConsumerEvent> sendMessages(@RequestBody ConsumerEvent e) {
        consumerService.sendMessage("patient_prescription_events", e);
        return ResponseEntity.ok(e);
    }
}

