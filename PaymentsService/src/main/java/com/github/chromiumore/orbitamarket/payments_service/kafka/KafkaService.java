package com.github.chromiumore.orbitamarket.payments_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, OrderPaymentResponse> kafkaTemplate;

    public void sendToKafka(String topic, OrderPaymentResponse event) {
        kafkaTemplate.send(topic, event);
    }
}
