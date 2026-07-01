package com.github.chromiumore.orbitamarket.payments_service.controller;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.dto.AccountDto;
import com.github.chromiumore.orbitamarket.payments_service.dto.BalanceDto;
import com.github.chromiumore.orbitamarket.payments_service.dto.TopUpRequest;
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
    public ResponseEntity<AccountDto> createAccount(@RequestHeader("X-User-Id") UUID userId) {
        PaymentAccount account = accountService.createAccount(userId);
        return ResponseEntity.ok(AccountDto.from(account));
    }

    @PostMapping("/accounts/top-up")
    public ResponseEntity<BalanceDto> topUp(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody TopUpRequest request
    ) {
        PaymentAccount account = accountService.topUp(userId, request);
        return ResponseEntity.ok(BalanceDto.from(account));
    }
}
