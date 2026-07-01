package com.github.chromiumore.orbitamarket.payments_service.dto;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;

import java.util.UUID;

public record BalanceDto(UUID userId, Double balance, String currency) {
    public static BalanceDto from(PaymentAccount account) {
        return new BalanceDto(account.getUserId(), account.getBalance(), "geocredits");
    }
}
