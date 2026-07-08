package com.github.chromiumore.orbitamarket.orders_service.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.outbox.OrderPaymentsOutbox;
import com.github.chromiumore.orbitamarket.orders_service.dto.event.OrderPaymentRequest;
import com.github.chromiumore.orbitamarket.orders_service.kafka.producer.OrderProducer;
import com.github.chromiumore.orbitamarket.orders_service.repository.outbox.OrderPaymentsOutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentsOutboxService {

    private final OrderPaymentsOutboxRepository outboxRepository;
    private final OrderProducer producer;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderPaymentsOutbox createOutboxEvent(Order order) {

        try {
            String payload = objectMapper.writeValueAsString(OrderPaymentRequest.from(order));
            OrderPaymentsOutbox outbox = new OrderPaymentsOutbox();
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
        List<OrderPaymentsOutbox> outboxEvents = outboxRepository.findBySentAtIsNull();

        for (OrderPaymentsOutbox event : outboxEvents) {
            try {
                OrderPaymentRequest paymentEvent = objectMapper.readValue(event.getPayload(), OrderPaymentRequest.class);
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
