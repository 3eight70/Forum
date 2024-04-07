package com.hits.common.Core.File.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель файла")
public class FileDto {
    @NotNull
    @Schema(description = "Идентификатор файла")
    private UUID id;

    @NotNull
    @Schema(description = "Дата загрузки файла")
    private LocalDateTime uploadTime = LocalDateTime.now();

    @NotNull
    @Schema(description = "Название файла")
    private String name;

    @NotNull
    @Schema(description = "Формат текста", example = "application/json")
    private String contentType;

    @NotNull
    @Schema(description = "Размер файла")
    private Long size;

    @NotNull
    @Schema(description = "Содержание файла")
    private byte[] fileContent;
}
