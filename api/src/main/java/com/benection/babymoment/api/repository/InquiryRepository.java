package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Inquiry> findByAccountIdOrderByInquiryDateDesc(int accountId);
}
