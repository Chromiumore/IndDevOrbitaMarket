package com.github.chromiumore.orbitamarket.orders_service.domain.order;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    PAYMENT_FAILED,
    REJECTED
}
