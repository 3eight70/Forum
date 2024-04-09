package com.hits.forum.Core.Message.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на редактирование сообщения")
public class EditMessageRequest {
    @Size(min = 1, message = "Минимальная длина сообщения равна 1")
    @Schema(description = "Текст сообщения")
    private String content;
}
