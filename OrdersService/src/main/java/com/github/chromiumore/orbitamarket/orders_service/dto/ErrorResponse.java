package com.github.chromiumore.orbitamarket.orders_service.dto;

public record ErrorResponse(String errorCode, String message, String timestamp) {
}
