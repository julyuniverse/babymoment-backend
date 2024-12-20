package com.benection.babymoment.api.repository;

import com.benection.babymoment.api.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    Activity findByActivityId(int activityId);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Activity> findTop100ByBabyIdAndStartTimeLessThanAndIsDeletedFalseOrderByStartTimeDesc(int babyId, LocalDateTime datetime);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Activity> findByBabyIdAndStartTimeBetweenAndIsDeletedFalseOrderByStartTimeDesc(int babyId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Query(value = """
            select a
            from Activity a
            where a.babyId = ?1
              and (a.startTime between ?2 and ?3
                or a.endTime between ?2 and ?3)
              and a.isDeleted = false
            order by a.startTime
            """)
    List<Activity> listByBabyIdAndStartTimeBetweenOrEndTimeBetweenAndIsActiveOrderByStarTime(int babyId, LocalDateTime starTime, LocalDateTime endTime);
}
