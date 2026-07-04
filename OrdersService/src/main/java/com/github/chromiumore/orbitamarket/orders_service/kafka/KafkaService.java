package com.github.chromiumore.orbitamarket.orders_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, OrderPaymentRequested> kafkaTemplate;

    public void sendToKafka(String topic, OrderPaymentRequested event) {
        kafkaTemplate.send(topic, event);
    }
}
