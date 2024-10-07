package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.BabyCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface BabyCodeRepository extends CrudRepository<BabyCode, String> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<BabyCode> findByAccountIdAndBabyId(int accountId, int babyId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    boolean existsByCode(String code);
}
