package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface VersionRepository extends JpaRepository<Version, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Version> findTopByPlatformAndIsMandatoryTrueAndIsActiveTrueOrderByReleaseDateDesc(String platform);
}
