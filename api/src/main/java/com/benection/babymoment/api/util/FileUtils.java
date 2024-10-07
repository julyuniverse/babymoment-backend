package com.benection.babymoment.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Slf4j
public class FileUtils {

    /**
     * 단일 파일을 저장한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    public static String uploadFile(MultipartFile multipartFile, String dir) throws IOException {
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        multipartFile.transferTo(new File(dir + fileName));

        return fileName;
    }

    /**
     * 특정 문자로 시작하는 파일을 찾아서 모두 삭제한다.
     *
     * @author Lee Taesung
     * @since 1.0
     */
    public static void deleteFilesWithPrefix(String directoryPath, String filePrefix) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            log.info("[deleteFilesWithPrefix] Invalid directory path: " + directoryPath);
            return;
        }
        File[] files = directory.listFiles((dir, name) -> name.startsWith(filePrefix));
        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    System.out.println("파일 삭제: " + file.getAbsolutePath());
                } else {
                    System.err.println("파일 삭제 실패: " + file.getAbsolutePath());
                }
            }
        }
    }
}
