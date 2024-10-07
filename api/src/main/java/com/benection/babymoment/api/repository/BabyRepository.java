package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface BabyRepository extends JpaRepository<Baby, Integer> {

    /**
     * @param babyId    Baby->babyId
     * @param isDeleted Baby->isDeleted
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Baby> findByBabyIdAndIsDeleted(long babyId, boolean isDeleted);

    /**
     * @param babyId Baby->babyId
     * @author Lee Taesung
     * @since 1.0
     */
    Baby findByBabyId(long babyId);
}
