package com.hits.FileSystem.Models.Dto.FileDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private UUID id;
    private String name;
    private Long size;
    private LocalDateTime uploadTime;
}
