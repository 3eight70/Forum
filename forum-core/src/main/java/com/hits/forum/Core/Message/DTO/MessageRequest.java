package com.hits.forum.Core.Message.DTO;

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
@Schema(description = "Запрос на создание сообщения")
public class MessageRequest {
    @Size(min = 1, message = "Минимальная длина сообщения равна 1")
    @NotNull(message = "Сообщение должно содержать не менее 1 символа")
    @Schema(description = "Текст сообщения")
    private String content;

    @NotNull(message = "id темы должен быть указан")
    @Schema(description = "Идентификатор темы")
    private UUID themeId;
}
