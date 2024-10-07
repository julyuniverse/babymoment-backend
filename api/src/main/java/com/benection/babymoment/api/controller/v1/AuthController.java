package com.benection.babymoment.api.controller.v1;

import com.benection.babymoment.api.dto.account.PasswordRecoveryRequest;
import com.benection.babymoment.api.dto.auth.*;
import com.benection.babymoment.api.dto.auth.TokenReissueRequest;
import com.benection.babymoment.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Tag(name = "Authentication/Authorization", description = "인증/인가 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "uuid로 로그인하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/login/uuid")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<UuidLoginResponse>> loginWithUuid(@RequestBody UuidLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithUuid(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "email로 회원가입하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/signup/email")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> signupWithEmail(@RequestBody EmailSignupRequest request) {
        return ResponseEntity.ok(authService.signupWithEmail(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "email로 로그인하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/login/email")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<EmailLoginResponse>> loginWithEmail(@RequestBody EmailLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithEmail(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "토큰 재발행하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/token/reissue")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<TokenReissueResponse>> reissueToken(@RequestBody TokenReissueRequest request) {
        return ResponseEntity.ok(authService.reissueToken(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "로그아웃하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> logout() {
        return ResponseEntity.ok(authService.logout());
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "비밀번호 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/password/find")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> processPasswordRecovery(@RequestBody PasswordRecoveryRequest request) {
        return ResponseEntity.ok(authService.processPasswordRecovery(request));
    }
}
