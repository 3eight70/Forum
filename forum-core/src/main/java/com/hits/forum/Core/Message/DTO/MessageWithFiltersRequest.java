package com.hits.forum.Core.Message.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Поиск сообщений с фильтрами")
public class MessageWithFiltersRequest {
    @Schema(description = "Текст сообщения")
    private String content;

    @Schema(description = "Минимальное время, в которое отправлено сообщение")
    private LocalDateTime timeFrom;

    @Schema(description = "Максимальное время, в которое отправлено сообщение")
    private LocalDateTime timeTo;

    @Schema(description = "Логин автора")
    private String authorLogin;

    @Schema(description = "Идентификатор темы")
    private UUID themeId;

    @Schema(description = "Идентификатор категории")
    private UUID categoryId;
}
