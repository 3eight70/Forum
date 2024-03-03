package com.hits.file.Mappers;

import com.hits.file.Models.Dto.FileDto.FileDto;
<<<<<<< HEAD
import com.hits.file.Models.Entity.File;
=======
import com.hits.file.Models.Entities.File;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class FileMapper {
<<<<<<< HEAD
    public static File multipartFileToFile(MultipartFile file, UUID userId) throws IOException {
=======
    public static File multipartFileToFile(MultipartFile file, String userId) throws IOException {
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
        return new File(UUID.randomUUID(),
                LocalDateTime.now(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getBytes(),
                userId
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
