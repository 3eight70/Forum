package com.hits.forum.Rest.Controllers.Message;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Enums.SortOrder;
import com.hits.forum.Core.Message.DTO.EditMessageRequest;
import com.hits.forum.Core.Message.DTO.MessageResponse;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import com.hits.forum.Core.Message.Service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Сообщения", description = "Отвечает за работу с сообщениями")
public class MessageController {
    private final MessageService messageService;

    @PostMapping(SEND_MESSAGE)
    @Operation(
            summary = "Отправка сообщения",
            description = "Позволяет создать и отправить сообщения"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "content") @Parameter(description = "Текст сообщения") String content,
            @RequestParam(name = "themeId") @Parameter(description = "Идентификатор темы") String themeId,
            @RequestParam(name = "file", required = false) @Parameter(description = "Список вложений") List<MultipartFile> files)
            throws NotFoundException, IOException {
        return messageService.createMessage(user, content, UUID.fromString(themeId), files);
    }

    @PutMapping(EDIT_MESSAGE)
    @Operation(
            summary = "Изменения сообщения",
            description = "Позволяет изменить сообщение"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> editMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "messageId") @Parameter(description = "Идентификатор сообщения") UUID messageId,
            @Valid @RequestBody EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException {
        return messageService.editMessage(user, messageId, editMessageRequest);
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
    public ResponseEntity<MessageResponse> getMessages(
            @PathVariable("themeId") @Parameter(description = "Идентификатор темы") UUID themeId,
            @RequestParam(name = "page", required = false, defaultValue = "0") @Parameter(description = "Номер страницы") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") @Parameter(description = "Количество элементов на странице") Integer size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateDesc") @Parameter(description = "Вариант сортировки") SortOrder sortOrder)
            throws NotFoundException{
        return messageService.getMessages(themeId, page, size, sortOrder);
    }

    @GetMapping(GET_MESSAGES_WITH_FILTERS)
    @Operation(
            summary = "Получение сообщений по фильтрам",
            description = "Позволяет получить сообщения по определенным фильтрам"
    )
    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(
            @RequestParam(value = "content", required = false) @Parameter(description = "Текст сообщения") String content,
            @RequestParam(value = "timeFrom", required = false) @Parameter(description = "Минимальное время, в которое отправлено сообщение") LocalDateTime timeFrom,
            @RequestParam(value = "timeTo", required = false) @Parameter(description = "Максимальное время, в которое отправлено сообщение") LocalDateTime timeTo,
            @RequestParam(value = "authorLogin", required = false) @Parameter(description = "Логин автора") String authorLogin,
            @RequestParam(value = "themeId", required = false) @Parameter(description = "Идентификатор темы") UUID themeId,
            @RequestParam(value = "categoryId", required = false) @Parameter(description = "Идентификатор категории") UUID categoryId
    ){
        return messageService.getMessagesWithFilters(content, timeFrom, timeTo, authorLogin, themeId, categoryId);
    }

    @GetMapping(GET_MESSAGES_WITH_SUBSTRING)
    @Operation(
            summary = "Поиск сообщение по подстроке",
            description = "Позволяет получить сообщения, используя поиск по подстроке"
    )
    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithSubstring(@RequestParam(value = "content") @Parameter(description = "Текст сообщения") String content){
        return messageService.getMessagesWithSubstring(content);
    }
}
