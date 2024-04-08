package com.hits.file.Core.File.Models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель файла")
public class FileDto {
    @Schema(description = "Идентификатор файла")
    private UUID id;

    @Schema(description = "Название файла")
    private String name;

    @Schema(description = "Размер файла")
    private Long size;

    @Schema(description = "Время загрузки в хранилище")
    private LocalDateTime uploadTime;
}
