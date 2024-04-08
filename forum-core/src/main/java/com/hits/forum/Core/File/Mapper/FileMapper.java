package com.hits.forum.Core.File.Mapper;

import com.hits.forum.Core.File.Entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public final class FileMapper {
    public static File multipartFileToFile(MultipartFile file){
        return new File(
                UUID.randomUUID(),
                file.getOriginalFilename(),
                file.getSize()
        );
    }
}
