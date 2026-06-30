package com.github.chromiumore.orbitamarket.payments_service.repository;

import com.github.chromiumore.orbitamarket.payments_service.domain.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {
    public Optional<PaymentAccount> findByUserId(UUID userId);
}
