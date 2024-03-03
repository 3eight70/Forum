package com.hits.forum.Models.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemeRequest {
    @Size(min = 5, message = "Минимальная длина названия темы равна 5")
    private String themeName;

    @NotNull(message = "id категории должен быть указан")
    private UUID categoryId;
}
