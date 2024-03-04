package com.hits.forum.Models.Dto.Message;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditMessageRequest {
    @Size(min = 1, message = "Минимальная длина сообщения равна 1")
    private String content;
}
