package com.hits.file.Mappers;

import com.hits.common.Models.User.UserDto;
import com.hits.file.Models.Dto.FileDto.FileDto;
import com.hits.file.Models.Entities.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class FileMapper {
    public static File multipartFileToFile(MultipartFile file, UserDto user) throws IOException {
        return new File(UUID.randomUUID(),
                LocalDateTime.now(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getBytes(),
                user.getId()
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
