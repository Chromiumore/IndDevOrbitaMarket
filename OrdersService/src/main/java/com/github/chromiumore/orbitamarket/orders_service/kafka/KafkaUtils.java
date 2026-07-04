package com.github.chromiumore.orbitamarket.orders_service.kafka;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;

import java.time.Instant;
import java.util.UUID;

public class KafkaUtils {
    public static OrderPaymentRequested createEvent(Order order) {
        return new OrderPaymentRequested(
                UUID.randomUUID(), order.getId(), order.getUserId(), order.getPrice(), Instant.now()
        );
    }
}
