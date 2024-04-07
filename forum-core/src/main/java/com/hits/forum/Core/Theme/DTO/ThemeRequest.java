package com.hits.forum.Core.Theme.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание темы")
public class ThemeRequest {
    @Size(min = 5, message = "Минимальная длина названия темы равна 5")
    @NotNull(message = "Минимальная длина названия темы равна 5")
    @Schema(description = "Название темы")
    private String themeName;

    @NotNull(message = "id категории должен быть указан")
    @Schema(description = "Идентификатор категории")
    private UUID categoryId;
}
