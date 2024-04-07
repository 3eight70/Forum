package com.hits.forum.Core.Message.DTO;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Page.DTO.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с сообщениями")
public class MessageResponse {
    @Schema(description = "Список сообщений")
    private List<MessageDto> messages;

    @Schema(description = "Пагинация")
    private PageResponse page;
}
