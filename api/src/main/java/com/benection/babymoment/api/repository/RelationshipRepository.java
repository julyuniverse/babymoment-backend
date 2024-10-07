package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Relationship;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Transactional
public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Relationship> findTopByAccountIdOrderByCreatedAt(int accountId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Relationship> findByAccountId(int accountId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    int countByAccountId(int accountId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Optional<Relationship> findByAccountIdAndBabyId(int accountId, int babyId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Relationship> findByBabyId(int babyId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    boolean existsByAccountIdAndBabyId(int accountId, int babyId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Query(value = """
            select b, r
            from Relationship r
            left join Baby b on b.babyId = r.babyId
            where r.accountId = ?1
            """)
    List<Object[]> babiesByAccountId(int accountId);
}
