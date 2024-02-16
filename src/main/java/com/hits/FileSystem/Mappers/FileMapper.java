package com.hits.FileSystem.Mappers;

import com.hits.FileSystem.Models.Entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class FileMapper {
    public static File multipartFileToFile(MultipartFile file) throws IOException {
        return new File(UUID.randomUUID(),
                LocalDateTime.now(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getBytes());
    }
}
