package com.benection.babymoment.api.service;

import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.inquiry.InquiryDto;
import com.benection.babymoment.api.dto.inquiry.InquiryListResponse;
import com.benection.babymoment.api.dto.inquiry.InquiryRequest;
import com.benection.babymoment.api.dto.Status;
import com.benection.babymoment.api.enums.StatusCode;
import com.benection.babymoment.api.entity.Inquiry;
import com.benection.babymoment.api.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.benection.babymoment.api.util.ConvertUtils.convertInquiryToInquiryDto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<Void> createInquiry(InquiryRequest request) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Inquiry inquiry = Inquiry.builder()
                .accountId(request.getAccountId())
                .babyId(request.getBabyId())
                .title(request.getTitle())
                .content(request.getContent())
                .inquiryDate(now)
                .build();
        inquiryRepository.save(inquiry);

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), null);
    }

    /**
     * @return 문의
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<InquiryListResponse> getInquiries(int accountId) {
        List<Inquiry> inquiries = inquiryRepository.findByAccountIdOrderByInquiryDateDesc(accountId);
        List<InquiryDto> inquiryDtos = new ArrayList<>();
        for (Inquiry inquiry : inquiries) {
            inquiryDtos.add(convertInquiryToInquiryDto(inquiry));
        }

        return new ApiResponse<>(new Status(StatusCode.SUCCESS), new InquiryListResponse(inquiryDtos));
    }
}
