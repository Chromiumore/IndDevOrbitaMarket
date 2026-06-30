package com.github.chromiumore.orbitamarket.payments_service.controller;

import com.github.chromiumore.orbitamarket.payments_service.domain.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentAccountController {

    private final PaymentAccountService accountService;

    @PostMapping
    public ResponseEntity<PaymentAccount> createAccount(@RequestHeader("X-User-Id") UUID userId) {
        PaymentAccount account = accountService.createAccount(userId);
        return ResponseEntity.ok(account);
    }
}
