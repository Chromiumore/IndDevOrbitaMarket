package com.github.chromiumore.orbitamarket.payments_service.service;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.dto.BalanceDto;
import com.github.chromiumore.orbitamarket.payments_service.dto.TopUpRequest;
import com.github.chromiumore.orbitamarket.payments_service.exception.AccountNotFoundException;
import com.github.chromiumore.orbitamarket.payments_service.exception.InsufficientBalanceException;
import com.github.chromiumore.orbitamarket.payments_service.exception.InvalidAmountException;
import com.github.chromiumore.orbitamarket.payments_service.repository.PaymentAccountRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Retryable(retryFor = { OptimisticLockException.class }, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public PaymentAccount topUp(UUID userId, TopUpRequest request) {
        Double amount = request.amount();
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
        PaymentAccount account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }

    @Retryable(retryFor = { OptimisticLockException.class }, maxAttempts = 5, backoff = @Backoff(delay = 100))
    @Transactional
    public PaymentAccount debitForOrder(UUID userId, Long orderId, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        PaymentAccount account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Double currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(currentBalance - amount);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalance(UUID userId) {
        PaymentAccount account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return BalanceDto.from(account);
    }
}
