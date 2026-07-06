package com.github.chromiumore.orbitamarket.payments_service.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OrderPaymentResponse(
        UUID eventId,
        Long orderId,
        UUID userId,
        Double amount,
        Double newBalance,
        String reason,
        String eventType
) {
}
