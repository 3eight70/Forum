package com.hits.file.Core.File.Mapper;

import com.hits.file.Core.File.Models.FileDto;
import com.hits.file.Core.File.Entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

public final class FileMapper {
    public static File multipartFileToFile(MultipartFile file, UUID messageId, UUID fileId) {
        return new File(fileId,
                LocalDateTime.now(),
                file.getOriginalFilename(),
                file.getSize(),
                messageId
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
