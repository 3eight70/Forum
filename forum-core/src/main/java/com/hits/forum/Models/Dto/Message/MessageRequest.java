package com.hits.forum.Models.Dto.Message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @Size(min = 1, message = "Минимальная длина сообщения равна 1")
    private String content;

    @NotNull(message = "id темы должен быть указан")
    private UUID themeId;
}
