package com.github.chromiumore.orbitamarket.payments_service.kafka.producer;

import com.github.chromiumore.orbitamarket.payments_service.dto.event.PaymentResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    public final static String PAYMENT_EVENTS_TOPIC = "order-payment-responses";
    private final KafkaTemplate<String, PaymentResponseEvent> kafkaTemplate;

    public void sendToKafka(PaymentResponseEvent event) {
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event);
    }
}
