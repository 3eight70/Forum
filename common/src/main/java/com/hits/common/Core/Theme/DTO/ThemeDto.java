package com.hits.common.Core.Theme.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель темы")
public class ThemeDto {
    @NotNull
    @Schema(description = "Идентификатор темы")
    private UUID themeId;

    @NotNull
    @Schema(description = "Время создания темы")
    private LocalDateTime createTime;

    @NotNull
    @Schema(description = "Время изменения темы")
    private LocalDateTime modifiedTime;

    @NotNull
    @Schema(description = "Название темы")
    private String themeName;

    @NotNull
    @Schema(description = "Идентификатор категории-родителя")
    private UUID categoryId;
}
