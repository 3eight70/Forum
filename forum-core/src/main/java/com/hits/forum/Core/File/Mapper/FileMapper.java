package com.hits.forum.Core.File.Mapper;

import com.hits.forum.Core.File.Entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FileMapper {
    public static File multipartFileToFile(MultipartFile file, UUID messageId){
        return new File(
                messageId,
                file.getOriginalFilename(),
                file.getSize()
        );
    }
}
