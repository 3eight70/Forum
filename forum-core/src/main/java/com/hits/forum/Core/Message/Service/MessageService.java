package com.hits.forum.Core.Message.Service;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Message.DTO.EditMessageRequest;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    ResponseEntity<?> createMessage(UserDto user, String content, UUID themeId)
            throws NotFoundException, BadRequestException;
    ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException, IOException;
    ResponseEntity<?> deleteMessage(UserDto user, UUID messageId)
            throws NotFoundException, ForbiddenException;
    ResponseEntity<Page<MessageDto>> getMessages(UUID themeId, Pageable pageable)
            throws NotFoundException;
    ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(MessageWithFiltersRequest request);
    ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithSubstring(String substring);
    ResponseEntity<MessageDto> checkMessage(UUID messageId) throws NotFoundException;
    ResponseEntity<?> deleteAttachmentFromMessage(UserDto user, UUID attachmentId, UUID messageId) throws IOException, NotFoundException;
    ResponseEntity<?> attachFilesToMessage(UserDto user, List<UUID> files, UUID messageId) throws NotFoundException;
}
