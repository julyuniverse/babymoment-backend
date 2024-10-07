package com.benection.babymoment.api.controller.v1;

import com.benection.babymoment.api.dto.activity.ActivityResponse;
import com.benection.babymoment.api.dto.activity.ActivityListResponse;
import com.benection.babymoment.api.dto.activity.ActivityRequest;
import com.benection.babymoment.api.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Tag(name = "Activity", description = "activity 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activities")
public class ActivityController {
    private final ActivityService activityService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "활동 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<ActivityResponse>> createActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.createActivity(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "활동 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PutMapping("/{activityId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<ActivityResponse>> updateActivity(@PathVariable int activityId, @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.updateActivity(activityId, request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "활동 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @DeleteMapping("/{activityId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> deleteActivity(@PathVariable int activityId) {
        return ResponseEntity.ok(activityService.deleteActivity(activityId));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "활동 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/last-date/{lastDate}/babies/{babyId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<ActivityListResponse>> getActivities(@PathVariable LocalDate lastDate, @PathVariable int babyId) {
        return ResponseEntity.ok(activityService.getActivities(lastDate, babyId));
    }
}
