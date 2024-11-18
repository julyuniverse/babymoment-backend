package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Account> findByEmail(String email);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Account findByAccountId(long accountId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    boolean existsByEmail(String email);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Account> findByAccountIdAndIsDeletedFalse(long accountId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Account> findByUserIdentifier(String userIdentifier);
}
