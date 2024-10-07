package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Device> findByUuid(String uuid);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Device findByDeviceId(int deviceId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Device> findByBabyId(int babyId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Device> findByAccountId(int accountId);
}
