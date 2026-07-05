package com.github.chromiumore.orbitamarket.orders_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.OrderStatus;
import com.github.chromiumore.orbitamarket.orders_service.repository.OrderRepository;
import com.github.chromiumore.orbitamarket.orders_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    public final static String PAYMENT_EVENTS_TOPIC = "order-payment-responses";

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = PAYMENT_EVENTS_TOPIC, groupId = "orders-group")
    public void handlePaymentEvent(ConsumerRecord<String, String> record) {
        String eventPayload = record.value();

        try {
            Map<String, Object> event = objectMapper.readValue(
                    eventPayload,
                    new TypeReference<Map<String, Object>>() {}
            );

            Long orderId = (Long) event.get("order_id");
            String eventType = (String) event.get("event_type");
            String failureReason = (String) event.get("reason");

            Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

            switch (eventType) {
                case "OrderPaymentCompleted":
                    order.setStatus(OrderStatus.PAID);
                    break;
                case "OrderPaymentFailed":
                    order.setStatus(OrderStatus.PAYMENT_FAILED);
                    order.setFailureReason(failureReason);
                    break;
                default:
                    log.warn("Unknown event type: {}", eventType);
                    return;
            }

            orderRepository.save(order);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse payment response message");
        }
    }

}
