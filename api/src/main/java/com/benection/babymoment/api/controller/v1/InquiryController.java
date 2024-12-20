package com.benection.babymoment.api.controller.v1;

import com.benection.babymoment.api.dto.inquiry.InquiryListResponse;
import com.benection.babymoment.api.dto.inquiry.InquiryRequest;
import com.benection.babymoment.api.service.InquiryService;
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
@Tag(name = "Inquiry", description = "inquiry 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/inquiries")
public class InquiryController {
    private final InquiryService inquiryService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "문의하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<Void>> createInquiry(@RequestBody InquiryRequest request) {
        return ResponseEntity.ok(inquiryService.createInquiry(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @Operation(summary = "문의 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<com.benection.babymoment.api.dto.ApiResponse<InquiryListResponse>> getInquiries(@PathVariable int accountId) {
        return ResponseEntity.ok(inquiryService.getInquiries(accountId));
    }
}
