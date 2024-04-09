package com.hits.forum.Core.File.Mapper;

import com.hits.common.Core.File.DTO.FileDto;
import com.hits.forum.Core.File.Entity.File;

public final class FileMapper {
    public static File fileDtoToFile(FileDto file){
        return new File(
                file.getId(),
                file.getName(),
                file.getSize()
        );
    }
}
