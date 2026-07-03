package com.github.chromiumore.orbitamarket.orders_service.exception;

public class MissingUserIdException extends RuntimeException {
    public MissingUserIdException(String message) {
        super(message);
    }
}
