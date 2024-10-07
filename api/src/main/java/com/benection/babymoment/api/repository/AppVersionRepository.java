package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface AppVersionRepository extends JpaRepository<AppVersion, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<AppVersion> findTopByPlatformAndIsMandatoryTrueAndIsActiveTrueOrderByReleaseDateDesc(String platform);
}
