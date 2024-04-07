package com.hits.forum.Core.Message.Service;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Enums.SortOrder;
import com.hits.forum.Core.Message.DTO.EditMessageRequest;
import com.hits.forum.Core.Message.DTO.MessageResponse;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    ResponseEntity<?> createMessage(UserDto user, String content, UUID themeId, List<MultipartFile> files)
            throws NotFoundException, IOException;
    ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException;
    ResponseEntity<?> deleteMessage(UserDto user, UUID messageId)
            throws NotFoundException, ForbiddenException;
    ResponseEntity<MessageResponse> getMessages(UUID themeId, Integer page, Integer size, SortOrder sortOrder)
            throws NotFoundException;
    ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(
            String content,
            LocalDateTime timeFrom,
            LocalDateTime timeTo,
            String authorLogin,
            UUID themeId,
            UUID categoryId);
    ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithSubstring(String substring);
    ResponseEntity<MessageDto> checkMessage(UUID messageId) throws NotFoundException;
}
