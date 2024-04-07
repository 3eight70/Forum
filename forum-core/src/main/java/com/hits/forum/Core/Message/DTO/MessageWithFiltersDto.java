package com.hits.forum.Core.Message.DTO;

import com.hits.forum.Core.File.Entity.File;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сообщения с фильтрами")
public class MessageWithFiltersDto {
    @Schema(description = "Идентификатор сообщения")
    private UUID id;

    @Schema(description = "Время создания сообщения")
    private LocalDateTime createTime;

    @Schema(description = "Время изменения сообщения")
    private LocalDateTime modifiedTime;

    @Schema(description = "Идентификатор темы")
    private UUID categoryId;

    @Schema(description = "Идентификатор темы")
    private UUID themeId;

    @Schema(description = "Логин автора")
    private String authorLogin;

    @Schema(description = "Текст сообщения")
    private String content;

    @Schema(description = "Список вложений")
    private List<File> files;
}
