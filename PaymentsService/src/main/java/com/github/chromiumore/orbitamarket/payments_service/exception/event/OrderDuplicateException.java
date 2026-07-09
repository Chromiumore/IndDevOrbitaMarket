package com.github.chromiumore.orbitamarket.payments_service.exception.event;

public class OrderDuplicateException extends RuntimeException {
    public OrderDuplicateException(String message) {
        super(message);
    }
}
