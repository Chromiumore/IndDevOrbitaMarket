package com.github.chromiumore.orbitamarket.payments_service.service;

import com.github.chromiumore.orbitamarket.payments_service.domain.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.exception.AccountNotFoundException;
import com.github.chromiumore.orbitamarket.payments_service.repository.PaymentAccountRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentAccountService {

    private PaymentAccountRepository accountRepository;

    public PaymentAccount createAccount(UUID userId) {
        PaymentAccount account = new PaymentAccount();
        account.setUserId(userId);
        return accountRepository.save(account);
    }

    public PaymentAccount getAccountByUserId(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account for user: " + userId + " does not exist"));
    }
}
