package com.benection.babymoment.api.service;

import com.benection.babymoment.api.dto.ApiResponse;
import com.benection.babymoment.api.dto.baby.*;
import com.benection.babymoment.api.dto.StatusDto;
import com.benection.babymoment.api.enums.*;
import com.benection.babymoment.api.entity.*;
import com.benection.babymoment.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.benection.babymoment.api.util.ConvertUtils.convertBabyToBabyDto;
import static com.benection.babymoment.api.util.DateUtils.applyUtcOffsetToKoreanTime;
import static com.benection.babymoment.api.util.FileUtils.deleteFilesWithPrefix;
import static com.benection.babymoment.api.util.HttpHeaderUtils.*;
import static com.benection.babymoment.api.util.RandomUtils.randomNumber;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BabyService {
    private final BabyRepository babyRepository;
    private final RelationshipRepository relationshipRepository;
    private final RelationshipHistoryRepository relationshipHistoryRepository;
    private final DeviceRepository deviceRepository;
    private final BabyCodeRepository babyCodeRepository;
    private final RedisTemplate<Object, Object> redisTemplate;
    @Value("${upload.path.baby.image}")
    private String babyImageUploadPath;


    /**
     * @return 아기
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<BabyResponse> createBaby(BabyCreationRequest request) throws IOException {
        ApiResponse<BabyResponse> apiResponse = new ApiResponse<>();

        // Get Datetime-Offset header value, Timezone-Identifier header value.
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();
        String imageFileName = "default.png";
        int babyCount = relationshipRepository.countByAccountId(request.getAccountId());
        if (babyCount >= 2) { // Baby creation is full.
            apiResponse.setStatus(new StatusDto(StatusCode.EXCEEDED_LIMIT_CREATION));

            return apiResponse;
        }

        // Check image.
        String extension = "";
        if (request.getImage() != null) {
            if (!request.getImage().isEmpty()) {
                // Check file extension.
                extension = FilenameUtils.getExtension(request.getImage().getOriginalFilename());
                if (!(Objects.equals(extension, "png") || Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg") || Objects.equals(extension, "PNG") || Objects.equals(extension, "JPG") || Objects.equals(extension, "JPEG"))) {
                    log.info("[createBaby] 유효하지 않은 확장자에요.");
                    apiResponse.setStatus(new StatusDto(StatusCode.UNSUPPORTED_EXTENSION));

                    return apiResponse;
                }
            }
        }

        // Create baby.
        Baby baby = Baby.builder()
                .name(request.getName())
                .birthday(applyUtcOffsetToKoreanTime(datetimeOffset, request.getBirthday()))
                .gender(Gender.valueOf(request.getGender()).name())
                .bloodType(BloodType.valueOf(request.getBloodType()).name())
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build();
        babyRepository.save(baby);

        // Update image.
        if (request.getImage() != null) {
            if (!request.getImage().isEmpty()) {
                String dir = babyImageUploadPath;

                // Delete existing file.
                deleteFilesWithPrefix(dir, String.valueOf(baby.getBabyId()));

                // Create file and update data.
                imageFileName = baby.getBabyId() + "_" + UUID.randomUUID();
                request.getImage().transferTo(new File(dir + imageFileName + "." + extension));
            }
        }

        // Create relationship.
        Relationship relationship = Relationship.builder()
                .accountId(request.getAccountId())
                .babyId(baby.getBabyId())
                .type(RelationshipType.valueOf(request.getRelationshipType()).name())
                .authority(Authority.ROLE_ADMIN.name())
                .build();
        relationshipRepository.save(relationship);

        // Create relationship history.
        relationshipHistoryRepository.save(RelationshipHistory.builder()
                .type(RelationshipHistoryType.CREATE.name())
                .accountId(request.getAccountId())
                .babyId(baby.getBabyId())
                .relationshipType(RelationshipType.valueOf(request.getRelationshipType()).name())
                .authority(Authority.ROLE_ADMIN.name())
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build());

        // Set return value.
        apiResponse.setStatus(new StatusDto(StatusCode.SUCCESS));
        apiResponse.setData(new BabyResponse(convertBabyToBabyDto(baby, relationship)));

        return apiResponse;
    }

    /**
     * @return 요청 결괏값
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<Void> deleteBaby(int babyId, int accountId) {
        // Get Datetime-Offset header value, Timezone-Identifier header value.
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();
        LocalDateTime datetime = LocalDateTime.now();

        // Update relationship.
        Optional<Relationship> optionalRelationship = relationshipRepository.findByAccountIdAndBabyId(accountId, babyId);
        if (optionalRelationship.isPresent()) {
            // admin 권한을 가진 account이라면 해당 baby로 등록된 모든 baby를 삭제한다.
            if (Objects.equals(optionalRelationship.get().getAuthority(), Authority.ROLE_ADMIN.name())) {
                // Delete baby.
                Baby baby = babyRepository.findByBabyId(optionalRelationship.get().getBabyId());
                baby.updateIsDeleted(true);
                baby.updateDeletedAt(datetime);

                // Set device baby id to null.
                List<Device> devices = deviceRepository.findByBabyId(baby.getBabyId());
                for (Device device : devices) {
                    device.updateBabyId(null);
                }

                // Delete relationship.
                List<Relationship> relationships = relationshipRepository.findByBabyId(baby.getBabyId());
                for (Relationship deleteRelationship : relationships) {
                    // Create relationship history.
                    relationshipHistoryRepository.save(RelationshipHistory.builder()
                            .type(RelationshipHistoryType.DELETE.name())
                            .accountId(deleteRelationship.getAccountId())
                            .babyId(deleteRelationship.getBabyId())
                            .relationshipType(RelationshipType.valueOf(deleteRelationship.getType()).name())
                            .authority(Authority.valueOf(deleteRelationship.getAuthority()).name())
                            .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                            .tzId(timezoneIdentifier)
                            .build());

                    // Delete relationship.
                    relationshipRepository.delete(deleteRelationship);
                }
            } else {
                // Create relationship history.
                relationshipHistoryRepository.save(RelationshipHistory.builder()
                        .type(RelationshipHistoryType.DELETE.name())
                        .accountId(optionalRelationship.get().getAccountId())
                        .babyId(optionalRelationship.get().getBabyId())
                        .relationshipType(RelationshipType.valueOf(optionalRelationship.get().getType()).name())
                        .authority(Authority.valueOf(optionalRelationship.get().getAuthority()).name())
                        .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                        .tzId(timezoneIdentifier)
                        .build());

                // Delete relationship.
                relationshipRepository.delete(optionalRelationship.get());
            }
        }

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), null);
    }


    /**
     * @return 아기 코드
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<CodeGenerationResponse> generateCode(CodeGenerationRequest request) {
        // 1-1. Check if there is data created based on (babycode:accountId:babyId) in redis.
        Optional<BabyCode> optionalBabyCode = babyCodeRepository.findByAccountIdAndBabyId(request.getAccountId(), request.getBabyId());
        if (optionalBabyCode.isPresent()) {
            // 1-2. If there is data, return data information.
            return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), new CodeGenerationResponse(optionalBabyCode.get().getCode(), redisTemplate.getExpire("babycode:" + optionalBabyCode.get().getCode(), TimeUnit.SECONDS)));
        } else {
            // 1-2. 데이터가 없다면 생성한다.
            String code;
            while (true) {
                code = String.valueOf(randomNumber(6));
                if (!babyCodeRepository.existsByCode(code)) {
                    break;
                }
            }
            BabyCode babyCode = BabyCode.builder()
                    .code(code)
                    .accountId(request.getAccountId())
                    .babyId(request.getBabyId())
                    .ttl(7200L)
                    .build();
            babyCodeRepository.save(babyCode);

            return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), new CodeGenerationResponse(babyCode.getCode(), redisTemplate.getExpire("babycode:" + babyCode.getCode(), TimeUnit.SECONDS)));
        }
    }

    /**
     * @return 아기
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<BabyResponse> shareCode(CodeSharingRequest codeSharingRequest) {
        ApiResponse<BabyResponse> apiResponse = new ApiResponse<>();

        // Get Datetime-Offset header value, Timezone-Identifier header value.
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        String timezoneIdentifier = getTimezoneIdentifier();
        Integer babyId;
        int babyCount = relationshipRepository.countByAccountId(codeSharingRequest.getAccountId());
        if (babyCount >= 2) { // 아기 생성 한도 초과
            apiResponse.setStatus(new StatusDto(StatusCode.EXCEEDED_LIMIT_CREATION));

            return apiResponse;
        }

        // redis에서 해당 코드로 데이터를 찾은 뒤 baby를 등록한다.
        Optional<BabyCode> babyCodeOptional = babyCodeRepository.findById(codeSharingRequest.getCode());
        if (babyCodeOptional.isPresent()) {
            babyId = babyCodeOptional.get().getBabyId();
        } else {
            apiResponse.setStatus(new StatusDto(StatusCode.NOT_FOUND_CODE));

            return apiResponse;
        }

        // Check baby.
        Baby baby = babyRepository.findByBabyId(babyId);
        if (baby.getIsDeleted()) {
            apiResponse.setStatus(new StatusDto(StatusCode.ALREADY_DELETED_BABY));

            return apiResponse;
        }

        // 이미 관계가 있는지 검사한다.
        if (relationshipRepository.existsByAccountIdAndBabyId(codeSharingRequest.getAccountId(), baby.getBabyId())) {
            apiResponse.setStatus(new StatusDto(StatusCode.ALREADY_REGISTERED_BABY));

            return apiResponse;
        }

        // Create relationship.
        Relationship relationship = Relationship.builder()
                .accountId(codeSharingRequest.getAccountId())
                .babyId(baby.getBabyId())
                .type(RelationshipType.valueOf(codeSharingRequest.getRelationshipType()).name())
                .authority(Authority.ROLE_USER.name())
                .build();
        relationshipRepository.save(relationship);

        // Create relationship history.
        relationshipHistoryRepository.save(RelationshipHistory.builder()
                .type(RelationshipHistoryType.CREATE.name())
                .accountId(codeSharingRequest.getAccountId())
                .babyId(baby.getBabyId())
                .relationshipType(RelationshipType.valueOf(codeSharingRequest.getRelationshipType()).name())
                .authority(Authority.ROLE_USER.name())
                .utcOffset(String.valueOf(datetimeOffset.getOffset()))
                .tzId(timezoneIdentifier)
                .build());

        // Set return value.
        apiResponse.setStatus(new StatusDto(StatusCode.SUCCESS));
        apiResponse.setData(new BabyResponse(convertBabyToBabyDto(baby, relationship)));

        return apiResponse;
    }

    /**
     * @return 해당 accountId의 baby들을 최대 2개까지 채워서 반환
     * @author Lee Taesung
     * @since 1.0
     */
    public ApiResponse<BabyListResponse> getBabies(int accountId) {
        List<Object[]> babies = relationshipRepository.babiesByAccountId(accountId);
        List<BabyDto> babyDtos = new ArrayList<>();
        int babyCount;
        for (int i = 0, j = 0; i < babies.size(); i++) { // 최대 2개까지
            if (j > 1) {
                break;
            }
            babyDtos.add(convertBabyToBabyDto((Baby) babies.get(i)[0], (Relationship) babies.get(i)[1]));
            j++;
        }
        babyCount = babyDtos.size();
        for (int i = 0; i < (2 - babyCount); i++) {
            BabyDto babyResponse = new BabyDto();
            babyResponse.setBabyId(babyResponse.getBabyId() - i);
            babyDtos.add(babyResponse);
        }

        return new ApiResponse<>(new StatusDto(StatusCode.SUCCESS), new BabyListResponse(babyDtos));
    }

    /**
     * @return 아기
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public ApiResponse<BabyResponse> changeBaby(int babyId, int accountId) {
        ApiResponse<BabyResponse> apiResponse = new ApiResponse<>();

        // Check baby.
        Baby baby = babyRepository.findByBabyId(babyId);
        if (baby.getIsDeleted()) {
            apiResponse.setStatus(new StatusDto(StatusCode.ALREADY_DELETED_BABY));

            return apiResponse;
        }

        // 해당 계정의 아기가 맞는지 확인한다.
        Relationship relationship;
        Optional<Relationship> optionalRelationship = relationshipRepository.findByAccountIdAndBabyId(accountId, babyId);
        if (optionalRelationship.isPresent()) {
            relationship = optionalRelationship.get();
        } else {
            apiResponse.setStatus(new StatusDto(StatusCode.NOT_FOUND_BABY));

            return apiResponse;
        }

        // Get Device-Id header value.
        int deviceId = Integer.parseInt(getDeviceId());

        // Update device.
        Device device = deviceRepository.findByDeviceId(deviceId);
        device.updateAccountId(accountId);
        device.updateBabyId(babyId);

        // Set return value.
        apiResponse.setStatus(new StatusDto(StatusCode.SUCCESS));
        apiResponse.setData(new BabyResponse(convertBabyToBabyDto(baby, relationship)));

        return apiResponse;
    }

    /**
     * @return 아기
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional(rollbackFor = {Exception.class})
    public com.benection.babymoment.api.dto.ApiResponse<BabyResponse> updateBaby(BabyUpdateRequest request) throws IOException {
        ApiResponse<BabyResponse> apiResponse = new ApiResponse<>();

        // Get Offset-Datetime header value.
        OffsetDateTime datetimeOffset = getDatetimeOffset();
        Baby baby = babyRepository.findByBabyId(request.getBabyId());
        String imageFileName = "default.png";

        // Check relationship.
        Optional<Relationship> optionalRelationship = relationshipRepository.findByAccountIdAndBabyId(request.getAccountId(), request.getBabyId());
        Relationship relationship;
        if (optionalRelationship.isPresent()) {
            relationship = optionalRelationship.get();
        } else {
            log.info("[updateBaby] 이미 삭제된 아기에요.");
            apiResponse.setStatus(new StatusDto(StatusCode.ALREADY_DELETED_BABY));

            return apiResponse;
        }

        // check image.
        if (request.getImage() != null) {
            if (!request.getImage().isEmpty()) {
                // check file extension.
                String extension = FilenameUtils.getExtension(request.getImage().getOriginalFilename());
                if (!(Objects.equals(extension, "png") || Objects.equals(extension, "jpg") || Objects.equals(extension, "jpeg") || Objects.equals(extension, "PNG") || Objects.equals(extension, "JPG") || Objects.equals(extension, "JPEG"))) {
                    log.info("[updateBaby] 유효하지 않은 확장자에요.");
                    apiResponse.setStatus(new StatusDto(StatusCode.UNSUPPORTED_EXTENSION));

                    return apiResponse;
                }
                String dir = babyImageUploadPath;

                // 기존 파일을 삭제한다.
                deleteFilesWithPrefix(dir, String.valueOf(baby.getBabyId()));

                // 파일 생성 및 데이터를 업데이트한다.
                imageFileName = baby.getBabyId() + "_" + UUID.randomUUID();
                request.getImage().transferTo(new File(dir + imageFileName + "." + extension));
            } else {
            }
        } else {
        }

        // 정보를 업데이트한다.
        if (request.getName() != null && !Objects.equals(request.getName(), baby.getName())) {
            baby.updateName(request.getName());
        }
        if (request.getBirthday() != null && !applyUtcOffsetToKoreanTime(datetimeOffset, request.getBirthday()).isEqual(baby.getBirthday())) {
            baby.updateBirthday(applyUtcOffsetToKoreanTime(datetimeOffset, request.getBirthday()));
        }
        if (request.getGender() != null && !Objects.equals(request.getGender(), baby.getGender())) {
            baby.updateGender(request.getGender());
        }
        if (request.getBloodType() != null && !Objects.equals(request.getBloodType(), baby.getBloodType())) {
            baby.updateBloodType(request.getBloodType());
        }
        if (request.getRelationshipType() != null && !Objects.equals(request.getRelationshipType(), relationship.getType())) {
            relationship.updateType(request.getRelationshipType());
        }

        // Set return value.
        apiResponse.setStatus(new StatusDto(StatusCode.SUCCESS));
        apiResponse.setData(new BabyResponse(convertBabyToBabyDto(baby, relationship)));

        return apiResponse;
    }
}
