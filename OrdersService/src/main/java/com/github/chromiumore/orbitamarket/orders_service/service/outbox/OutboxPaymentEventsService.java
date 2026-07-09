package com.github.chromiumore.orbitamarket.orders_service.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.outbox.OutboxPaymentEvent;
import com.github.chromiumore.orbitamarket.orders_service.dto.event.PaymentRequestedEvent;
import com.github.chromiumore.orbitamarket.orders_service.kafka.producer.PaymentEventProducer;
import com.github.chromiumore.orbitamarket.orders_service.repository.outbox.OutboxPaymentEventsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPaymentEventsService {

    private final OutboxPaymentEventsRepository outboxRepository;
    private final PaymentEventProducer producer;
    private final ObjectMapper objectMapper;

    @Transactional
    public OutboxPaymentEvent createOutboxEvent(Order order) {

        try {
            String payload = objectMapper.writeValueAsString(PaymentRequestedEvent.from(order));
            OutboxPaymentEvent outbox = new OutboxPaymentEvent();
            outbox.setAggregateId(order.getId());
            outbox.setEventType("OrderPaymentRequested");
            outbox.setPayload(payload);
            outbox.setCreatedAt(Instant.now());

            return outboxRepository.save(outbox);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishAllPendingEvents() {
        List<OutboxPaymentEvent> outboxEvents = outboxRepository.findBySentAtIsNull();

        for (OutboxPaymentEvent event : outboxEvents) {
            try {
                PaymentRequestedEvent paymentEvent = objectMapper.readValue(event.getPayload(), PaymentRequestedEvent.class);
                producer.sendToKafka(paymentEvent);

                event.setSentAt(Instant.now());
                outboxRepository.save(event);
                log.info("Outbox event {} sent to Kafka", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
            }
        }
    }
}
