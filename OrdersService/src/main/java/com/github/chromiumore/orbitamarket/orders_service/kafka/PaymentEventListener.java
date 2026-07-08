package com.github.chromiumore.orbitamarket.orders_service.kafka;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.OrderStatus;
import com.github.chromiumore.orbitamarket.orders_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    public final static String PAYMENT_EVENTS_TOPIC = "order-payment-responses";

    private final OrderRepository orderRepository;

    @KafkaListener(topics = PAYMENT_EVENTS_TOPIC, groupId = "orders-group")
    public void handlePaymentEvent(OrderPaymentResponse event) {

        String eventType = event.eventType();

        Order order = orderRepository.findById(event.orderId()).orElseThrow(() -> new RuntimeException("Order not found"));

        switch (eventType) {
            case "OrderPaymentCompleted":
                order.setStatus(OrderStatus.PAID);
                break;
            case "OrderPaymentFailed":
                order.setStatus(OrderStatus.PAYMENT_FAILED);
                order.setFailureReason(event.reason());
                break;
            default:
                log.warn("Unknown event type: {}", eventType);
                return;
        }

        orderRepository.save(order);
    }

}
