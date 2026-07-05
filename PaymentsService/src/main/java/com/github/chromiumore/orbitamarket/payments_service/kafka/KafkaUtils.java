package com.github.chromiumore.orbitamarket.payments_service.kafka;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;

import java.util.UUID;

public class KafkaUtils {
    public static OrderPaymentResponse createCompletedEvent(PaymentAccount account, Long orderId, Double amount) {
        return new OrderPaymentResponse(
                UUID.randomUUID(), orderId, account.getUserId(), amount, account.getBalance(), null, "OrderPaymentCompleted"
        );
    }

    public static OrderPaymentResponse createFailedEvent(UUID userId, Long orderId, String reason) {
        return new OrderPaymentResponse(
                UUID.randomUUID(), orderId, userId, null, null, reason, "OrderPaymentFailed"
        );
    }
}
