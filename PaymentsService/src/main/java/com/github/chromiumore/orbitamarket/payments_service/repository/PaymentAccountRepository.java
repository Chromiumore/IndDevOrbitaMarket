package com.github.chromiumore.orbitamarket.payments_service.repository;

import com.github.chromiumore.orbitamarket.payments_service.domain.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO payments_schema.payment_accounts (user_id, amount, created_at)
        VALUES (:userId, 0, now())
        ON CONFLICT (user_id) DO NOTHING
        """, nativeQuery = true)
    void insertIgnore(@Param("userId") UUID userId);

    Optional<PaymentAccount> findByUserId(UUID userId);
}
