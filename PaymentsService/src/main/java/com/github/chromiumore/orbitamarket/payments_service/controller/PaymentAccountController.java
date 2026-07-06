package com.github.chromiumore.orbitamarket.payments_service.controller;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.dto.AccountDto;
import com.github.chromiumore.orbitamarket.payments_service.dto.BalanceDto;
import com.github.chromiumore.orbitamarket.payments_service.dto.TopUpRequest;
import com.github.chromiumore.orbitamarket.payments_service.exception.MissingUserIdException;
import com.github.chromiumore.orbitamarket.payments_service.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentAccountController {

    private final PaymentAccountService accountService;

    @PostMapping("/accounts")
    public ResponseEntity<AccountDto> createAccount(@RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new MissingUserIdException("X-User-Id is not provided");
        }
        PaymentAccount account = accountService.createAccount(userId);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    @PostMapping("/accounts/top-up")
    public ResponseEntity<BalanceDto> topUp(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestBody TopUpRequest request
    ) {
        if (userId == null) {
            throw new MissingUserIdException("X-User-Id is not provided");
        }
        PaymentAccount account = accountService.topUp(userId, request);
        return ResponseEntity.ok(BalanceDto.from(account));
    }

    @GetMapping("/accounts/balance")
    public ResponseEntity<BalanceDto> getBalance(@RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new MissingUserIdException("X-User-Id is not provided");
        }
        BalanceDto balance = accountService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}
