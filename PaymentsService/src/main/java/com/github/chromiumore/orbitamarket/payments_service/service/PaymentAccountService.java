package com.github.chromiumore.orbitamarket.payments_service.service;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.dto.BalanceDto;
import com.github.chromiumore.orbitamarket.payments_service.dto.TopUpRequest;
import com.github.chromiumore.orbitamarket.payments_service.dto.event.PaymentRequestedEvent;
import com.github.chromiumore.orbitamarket.payments_service.exception.AccountNotFoundException;
import com.github.chromiumore.orbitamarket.payments_service.exception.InsufficientBalanceException;
import com.github.chromiumore.orbitamarket.payments_service.exception.InvalidAmountException;
import com.github.chromiumore.orbitamarket.payments_service.exception.event.EventDuplicateException;
import com.github.chromiumore.orbitamarket.payments_service.exception.event.OrderDuplicateException;
import com.github.chromiumore.orbitamarket.payments_service.repository.PaymentAccountRepository;
import com.github.chromiumore.orbitamarket.payments_service.service.inbox.PaymentEventsInboxService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentAccountService {

    private final PaymentAccountRepository accountRepository;
    private final PaymentEventsInboxService inboxService;

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
    @Transactional(noRollbackFor = { InvalidAmountException.class, AccountNotFoundException.class, InsufficientBalanceException.class })
    public PaymentAccount processDebitEvent(PaymentRequestedEvent event) {

        UUID eventId = event.eventId();
        Long orderId = event.orderId();

        if (inboxService.getInboxEventByEventId(eventId).isPresent()) {
            log.error("Event with id={} already has been processed", eventId);
            throw new EventDuplicateException("Event already has been processed");
        }

        if (inboxService.getInboxEventByOrderId(orderId).isPresent()) {
            log.error("Event of order with id={} already has been processed", orderId);
            throw new OrderDuplicateException("Order already has been processed");
        }

        inboxService.createInboxEvent(event);

        Double amount = event.amount();

        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        PaymentAccount account = accountRepository.findByUserId(event.userId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Double currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(currentBalance - amount);
        account = accountRepository.save(account);

        return account;
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalance(UUID userId) {
        PaymentAccount account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return BalanceDto.from(account);
    }
}
