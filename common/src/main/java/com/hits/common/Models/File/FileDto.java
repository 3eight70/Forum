package com.hits.common.Models.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private UUID id;
    private LocalDateTime uploadTime = LocalDateTime.now();
    private String name;
    private String contentType;
    private Long size;
    private byte[] fileContent;
}
