package com.github.chromiumore.orbitamarket.orders_service.kafka;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OrderPaymentRequest(
        UUID eventId,
        Long orderId,
        UUID userId,
        Double amount,
        Instant occurredAt
) {
}
