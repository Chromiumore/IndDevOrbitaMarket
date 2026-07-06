package com.github.chromiumore.orbitamarket.payments_service.repository;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import jakarta.transaction.Transactional;
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
        INSERT INTO payments_schema.payment_accounts (user_id, balance, created_at, version)
        VALUES (:userId, 0, now(), 0)
        ON CONFLICT (user_id) DO NOTHING
        """, nativeQuery = true)
    void insertIgnore(@Param("userId") UUID userId);

    @Modifying
    @Query(value = """
            UPDATE PaymentAccount a
            SET a.balance = a.balance + :delta, a.version = a.version + 1
            WHERE a.userId = :userId AND a.version = :version
            """)
    int updateBalanceWithVersion(@Param("userId") UUID userId,
                                 @Param("delta") Double delta,
                                 @Param("version") Long version);

    Optional<PaymentAccount> findByUserId(UUID userId);
}
