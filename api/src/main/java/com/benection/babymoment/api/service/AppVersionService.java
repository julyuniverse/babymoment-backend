package com.benection.babymoment.api.service;

import com.benection.babymoment.api.entity.AppVersion;
import com.benection.babymoment.api.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.benection.babymoment.api.util.VersionUtils.toNumber;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AppVersionService {
    private final AppVersionRepository appVersionRepository;

    /**
     * 현재 버전이 업데이트가 필수인지 확인한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    public boolean isUpdateMandatory(String platform, String currentVersion) {
        // 가장 최신 필수 업데이트 버전을 가져온다.
        Optional<AppVersion> optionalLatestMandatoryVersion = appVersionRepository.findTopByPlatformAndIsMandatoryTrueAndIsActiveTrueOrderByReleaseDateDesc(platform);

        // 현재 버전이 최신 필수 업데이트 버전보다 낮은지 확인한다.
        return optionalLatestMandatoryVersion.filter(appVersion -> toNumber(currentVersion) < toNumber(appVersion.getVersion())).isPresent();
    }
}