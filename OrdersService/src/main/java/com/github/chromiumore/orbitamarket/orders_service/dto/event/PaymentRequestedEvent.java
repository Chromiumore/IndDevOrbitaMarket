package com.github.chromiumore.orbitamarket.orders_service.dto.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentRequestedEvent(
        UUID eventId,
        Long orderId,
        UUID userId,
        Double amount,
        Instant occurredAt
) {
    public static PaymentRequestedEvent from(Order order) {
        return new PaymentRequestedEvent(
                UUID.randomUUID(), order.getId(), order.getUserId(), order.getPrice(), Instant.now()
        );
    }
}
