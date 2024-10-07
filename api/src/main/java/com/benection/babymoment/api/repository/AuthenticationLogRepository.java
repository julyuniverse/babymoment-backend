package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.AuthenticationLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface AuthenticationLogRepository extends JpaRepository<AuthenticationLog, Integer> {
}
