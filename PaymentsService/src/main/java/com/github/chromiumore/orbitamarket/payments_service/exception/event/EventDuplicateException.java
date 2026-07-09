package com.github.chromiumore.orbitamarket.payments_service.exception.event;

public class EventDuplicateException extends RuntimeException {
    public EventDuplicateException(String message) {
        super(message);
    }
}
