package york.pharmacy.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, ProducerEvent> kafkaTemplate;

    public void sendMessage(String topic, ProducerEvent e) {
        kafkaTemplate.send(topic, e);
//        System.out.println("Message sent: " + e);
    }
}
