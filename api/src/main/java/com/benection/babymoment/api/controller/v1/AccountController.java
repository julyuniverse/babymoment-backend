package com.benection.babymoment.api.controller.v1;

import com.benection.babymoment.api.dto.AccountRequest;
import com.benection.babymoment.api.dto.AccountResponse;
import com.benection.babymoment.api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Tag(name = "Account", description = "account 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "계정 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<AccountResponse>> getAccount(@PathVariable int accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "계정 업데이트하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PutMapping("/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<AccountResponse>> updateAccount(@PathVariable int accountId, @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(accountId, request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "계정 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> deleteAccount(@PathVariable int accountId) {
        return ResponseEntity.ok(accountService.deleteAccount(accountId));
    }
}
