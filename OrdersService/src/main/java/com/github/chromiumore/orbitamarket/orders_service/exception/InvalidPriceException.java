package com.github.chromiumore.orbitamarket.orders_service.exception;

public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String message) {
        super(message);
    }
}
