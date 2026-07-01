package com.github.chromiumore.orbitamarket.payments_service.exception;

public class MissingUserIdException extends RuntimeException {
    public MissingUserIdException(String message) {
        super(message);
    }
}
