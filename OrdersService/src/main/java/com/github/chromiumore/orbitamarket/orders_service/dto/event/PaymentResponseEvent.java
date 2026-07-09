package com.github.chromiumore.orbitamarket.orders_service.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentResponseEvent(
        UUID eventId,
        Long orderId,
        UUID userId,
        Double amount,
        Double newBalance,
        String reason,
        String eventType
) {
}
