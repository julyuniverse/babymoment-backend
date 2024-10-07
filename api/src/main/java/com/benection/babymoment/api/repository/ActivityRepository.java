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
    List<Activity> findTop100ByBabyIdAndStartTimeLessThanAndIsActiveOrderByStartTimeDesc(int babyId, LocalDateTime datetime, boolean isActive);

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    List<Activity> findByBabyIdAndStartTimeBetweenAndIsActiveOrderByStartTimeDesc(int babyId, LocalDateTime startTime, LocalDateTime endTime, boolean isActive);

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
              and a.isActive = ?4
            order by a.startTime
            """)
    List<Activity> listByBabyIdAndStartTimeBetweenOrEndTimeBetweenAndIsActiveOrderByStarTime(int babyId, LocalDateTime starTime, LocalDateTime endTime, boolean isActive);
}
