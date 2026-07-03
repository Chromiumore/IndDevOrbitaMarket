package com.github.chromiumore.orbitamarket.orders_service.exception;

import com.github.chromiumore.orbitamarket.orders_service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> errorResponse(HttpStatus status, String code, String message) {
        ErrorResponse body = new ErrorResponse(code, message, Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException e) {
        return errorResponse(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", e.getMessage());
    }
}
