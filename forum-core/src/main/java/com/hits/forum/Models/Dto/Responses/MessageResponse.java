package com.hits.forum.Models.Dto.Responses;

import com.hits.common.Models.Message.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private List<MessageDto> messages;
    private PageResponse page;
}
