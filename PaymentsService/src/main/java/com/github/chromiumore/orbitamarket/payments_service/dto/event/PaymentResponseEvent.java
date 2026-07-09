package com.github.chromiumore.orbitamarket.payments_service.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;

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
    public static PaymentResponseEvent createCompletedEvent(PaymentAccount account, Long orderId, Double amount) {
        return new PaymentResponseEvent(
                UUID.randomUUID(), orderId, account.getUserId(), amount, account.getBalance(), null, "OrderPaymentCompleted"
        );
    }

    public static PaymentResponseEvent createFailedEvent(UUID userId, Long orderId, String reason) {
        return new PaymentResponseEvent(
                UUID.randomUUID(), orderId, userId, null, null, reason, "OrderPaymentFailed"
        );
    }
}
