package com.hits.forum.Rest.Controllers.Message;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Message.DTO.EditMessageRequest;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersRequest;
import com.hits.forum.Core.Message.Service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Сообщения", description = "Отвечает за работу с сообщениями")
public class MessageController {
    private final MessageService messageService;

    @PostMapping(value = SEND_MESSAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Отправка сообщения",
            description = "Позволяет создать и отправить сообщения"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestPart(name = "content") @Parameter(description = "Текст сообщения") String content,
            @RequestPart(name = "themeId") @Parameter(description = "Идентификатор темы") String themeId,
            @RequestPart(name = "file", required = false) @Parameter(description = "Список вложений") List<MultipartFile> files)
            throws NotFoundException, IOException {
        return messageService.createMessage(user, content, UUID.fromString(themeId), files);
    }

    @PutMapping(value = EDIT_MESSAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Изменение сообщения",
            description = "Позволяет изменить сообщение"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> editMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "messageId") @Parameter(description = "Идентификатор сообщения") UUID messageId,
            @RequestParam(name = "file", required = false) @Parameter(description = "Список вложений") List<MultipartFile> files,
            @RequestParam(name = "content") @Parameter(description = "Текст сообщения") EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException, IOException {
        return messageService.editMessage(user, messageId, files, editMessageRequest);
    }

    @DeleteMapping(DELETE_MESSAGE)
    @Operation(
            summary = "Удаление сообщения",
            description = "Позволяет удалить сообщение"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "messageId") @Parameter(description = "Идентификатор сообщения") UUID messageId)
            throws NotFoundException, ForbiddenException{
        return messageService.deleteMessage(user, messageId);
    }

    @GetMapping(GET_MESSAGES)
    @Operation(
            summary = "Получение сообщений в теме",
            description = "Позволяет получить сообщения в теме, с использованием пагинации и сортировки"
    )
    public ResponseEntity<Page<MessageDto>> getMessages(
            @PathVariable("themeId") @Parameter(description = "Идентификатор темы") UUID themeId,
            @ParameterObject @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable
    )
            throws NotFoundException{
        return messageService.getMessages(themeId, pageable);
    }

    @PostMapping(GET_MESSAGES_WITH_FILTERS)
    @Operation(
            summary = "Получение сообщений по фильтрам",
            description = "Позволяет получить сообщения по определенным фильтрам"
    )
    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(@RequestBody MessageWithFiltersRequest request){
        return messageService.getMessagesWithFilters(request);
    }

    @GetMapping(GET_MESSAGES_WITH_SUBSTRING)
    @Operation(
            summary = "Поиск сообщение по подстроке",
            description = "Позволяет получить сообщения, используя поиск по подстроке"
    )
    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithSubstring(
            @RequestParam(value = "content") @Parameter(description = "Текст сообщения") String content){
        return messageService.getMessagesWithSubstring(content);
    }

    @DeleteMapping(DELETE_ATTACHMENT)
    @Operation(
            summary = "Удаление вложения из сообщения",
            description = "Позволяет удалить выбранное вложение"
    )
    public ResponseEntity<?> deleteAttachmentFromFile(
            @AuthenticationPrincipal UserDto userDto,
            @RequestParam(name = "messageId") @Parameter(description = "Идентификатор сообщения") UUID messageId,
            @RequestParam(name = "fileId") @Parameter(description = "Идентификатор файла") UUID fileId
            ) throws IOException, NotFoundException{
        return messageService.deleteAttachmentFromMessage(userDto, fileId, messageId);
    }
}
