package com.benection.babymoment.api.controller.v1;

import com.benection.babymoment.api.dto.pattern.DailyPatternListResponse;
import com.benection.babymoment.api.dto.pattern.DailyPatternResponse;
import com.benection.babymoment.api.service.PatternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Tag(name = "Pattern", description = "pattern 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patterns")
public class PatternController {
    private final PatternService patternService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "일간 패턴 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/daily/{date}/babies/{babyId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<DailyPatternResponse>> getDailyPatterns(@PathVariable LocalDate date, @PathVariable int babyId) {
        return ResponseEntity.ok(patternService.getDailyPatterns(date, babyId));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "주간 패턴 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/weekly/{startDate}/{endDate}/babies/{babyId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<DailyPatternListResponse>> getWeeklyPatterns(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate, @PathVariable int babyId) {
        return ResponseEntity.ok(patternService.getWeeklyPatterns(startDate, endDate, babyId));
    }
}
