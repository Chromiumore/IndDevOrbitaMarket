package com.github.chromiumore.orbitamarket.payments_service.dto;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;

import java.util.UUID;

public record AccountDto(Long id, UUID userId, Double balance) {
    public static AccountDto from(PaymentAccount account) {
        return new AccountDto(account.getId(), account.getUserId(), account.getBalance());
    }
}
