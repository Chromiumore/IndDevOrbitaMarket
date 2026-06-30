package com.github.chromiumore.orbitamarket.payments_service.service;

import com.github.chromiumore.orbitamarket.payments_service.domain.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.repository.PaymentAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentAccountService {

    private final PaymentAccountRepository accountRepository;

    @Transactional
    public PaymentAccount createAccount(UUID userId) {
        accountRepository.insertIgnore(userId);
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Failed to create account"));
    }
}
