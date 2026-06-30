package com.github.chromiumore.orbitamarket.payments_service.dto;

public record ErrorResponse(String error_code, String message, String timestamp) {
}
