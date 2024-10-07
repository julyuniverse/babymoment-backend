package com.benection.babymoment.api.controller.v1;

import com.benection.babymoment.api.dto.baby.*;
import com.benection.babymoment.api.service.BabyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Tag(name = "Baby", description = "baby 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/babies")
public class BabyController {
    private final BabyService babyService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<BabyResponse>> createBaby(BabyCreationRequest request) throws IOException {
        return ResponseEntity.ok(babyService.createBaby(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @DeleteMapping("/{babyId}/accounts/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> deleteBaby(@PathVariable int babyId, @PathVariable int accountId) {
        return ResponseEntity.ok(babyService.deleteBaby(babyId, accountId));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 코드 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/code/generate")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<CodeGenerationResponse>> generateCode(@RequestBody CodeGenerationRequest request) {
        return ResponseEntity.ok(babyService.generateCode(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 코드 공유하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/code/share")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<BabyResponse>> shareCode(@RequestBody CodeSharingRequest request) {
        return ResponseEntity.ok(babyService.shareCode(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<BabyListResponse>> getBabies(@PathVariable int accountId) {
        return ResponseEntity.ok(babyService.getBabies(accountId));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 변경하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PutMapping("/{babyId}/accounts/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<BabyResponse>> changeBaby(@PathVariable int babyId, @PathVariable int accountId) {
        return ResponseEntity.ok(babyService.changeBaby(babyId, accountId));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "아기 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/update")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<BabyResponse>> updateBaby(BabyUpdateRequest request) throws IOException {
        return ResponseEntity.ok(babyService.updateBaby(request));
    }
}
