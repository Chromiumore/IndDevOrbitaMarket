package com.github.chromiumore.orbitamarket.payments_service.dto;

public record ErrorResponse(String errorCode, String message, String timestamp) {
}
