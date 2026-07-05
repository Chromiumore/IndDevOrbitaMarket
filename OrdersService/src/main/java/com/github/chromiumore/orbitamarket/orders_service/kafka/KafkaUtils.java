package com.github.chromiumore.orbitamarket.orders_service.kafka;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;

import java.time.Instant;
import java.util.UUID;

public class KafkaUtils {
    public static OrderPaymentRequest createEvent(Order order) {
        return new OrderPaymentRequest(
                UUID.randomUUID(), order.getId(), order.getUserId(), order.getPrice(), Instant.now()
        );
    }
}
