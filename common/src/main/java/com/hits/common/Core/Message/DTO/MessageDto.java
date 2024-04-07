package com.hits.common.Core.Message.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель сообщения")
public class MessageDto {
    @Schema(description = "Идентификатор сообщения")
    @NotNull
    private UUID id;

    @Schema(description = "Время создания сообщения")
    @NotNull
    private LocalDateTime createTime;

    @Schema(description = "Время изменения сообщения")
    private LocalDateTime modifiedTime;

    @NotNull
    @Schema(description = "Логин автора сообщения")
    private String authorLogin;

    @NotNull
    @Schema(description = "Текст сообщения")
    @Size(min = 1, message = "Минимальная длина сообщения равна 1")
    private String content;

    @NotNull
    @Schema(description = "Идентификатор категории, в которой находится тема сообщения")
    private UUID categoryId;

    @NotNull
    @Schema(description = "Идентификатор темы-родителя")
    private UUID themeId;

    @Schema(description = "Список вложений")
    private List<UUID> files;
}

