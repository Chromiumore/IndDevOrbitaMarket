package com.github.chromiumore.orbitamarket.payments_service.exception;

import com.github.chromiumore.orbitamarket.payments_service.dto.ErrorResponse;
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

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException e) {
        return errorResponse(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", e.getMessage());
    }

    @ExceptionHandler(MissingUserIdException.class)
    public ResponseEntity<ErrorResponse> handleMissingUserId(MissingUserIdException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, "MISSING_USER_ID", e.getMessage());
    }
}
