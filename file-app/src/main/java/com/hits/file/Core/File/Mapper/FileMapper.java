package com.hits.file.Core.File.Mapper;

import com.hits.common.Core.File.DTO.FileDto;
import com.hits.file.Core.File.Entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

public final class FileMapper {
    public static File multipartFileToFile(MultipartFile file, String authorLogin) {
        return new File(UUID.randomUUID(),
                LocalDateTime.now(),
                file.getOriginalFilename(),
                file.getSize(),
                authorLogin
        );
    }

    public static FileDto fileToFileDto(File file) {
        return new FileDto(
                file.getId(),
                file.getName(),
                file.getSize(),
                file.getUploadTime()
        );
    }
}
