package com.hits.forum.Core.Message.Service;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Page.DTO.PageResponse;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.FileLimitException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Category.Repository.CategoryRepository;
import com.hits.forum.Core.Enums.SortOrder;
import com.hits.forum.Core.File.Entity.File;
import com.hits.forum.Core.File.Mapper.FileMapper;
import com.hits.forum.Core.File.Repository.FileRepository;
import com.hits.forum.Core.Message.DTO.EditMessageRequest;
import com.hits.forum.Core.Message.DTO.MessageResponse;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import com.hits.forum.Core.Message.Entity.ForumMessage;
import com.hits.forum.Core.Message.Mapper.MessageMapper;
import com.hits.forum.Core.Message.Repository.MessageRepository;
import com.hits.forum.Core.Theme.Entity.ForumTheme;
import com.hits.forum.Core.Theme.Repository.ThemeRepository;
import com.hits.forum.Core.Utils.ComparatorProvider;
import com.hits.security.Rest.Client.FileAppClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{
    private final CategoryRepository categoryRepository;
    private final ThemeRepository themeRepository;
    private final MessageRepository messageRepository;
    private final FileRepository fileRepository;
    private final FileAppClient fileAppClient;

    @Transactional
    public ResponseEntity<?> createMessage(UserDto user, String content, UUID themeId, List<MultipartFile> files)
            throws NotFoundException, IOException, BadRequestException {
        if (files != null && files.size() > 5){
            throw new FileLimitException();
        }

        if (content.isEmpty()){
            throw new BadRequestException("Минимальная длина сообщения равна 1");
        }

        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Тема-родитель с id=%s не существует", themeId));
        }
        else if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        ForumMessage forumMessage = MessageMapper.messageRequestToForumTheme(user.getLogin(), content,
                themeId, forumTheme.getCategoryId());
        UUID messageId = forumMessage.getId();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                UUID fileId = fileAppClient.uploadFile(messageId.toString(), file);
                File currentFile = FileMapper.multipartFileToFile(file, fileId);
                fileRepository.saveAndFlush(currentFile);
                forumMessage.getFiles().add(currentFile);
            }
        }

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно создано"), HttpStatus.OK);
    }

//    @Transactional
//    public ResponseEntity<?> deleteAttachmentFromMessage(UserDto user, UUID attachmentId, UUID messageId) {
//        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);
//
//        if (forumMessage == null) {
//            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
//        }
//
//        if (user.getRole() != Role.ADMIN) {
//            if  (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin()) ||
//                    user.getRole() != Role.MODERATOR || user.getManageCategoryId() != forumMessage.getCategoryId()) {
//                throw new ForbiddenException();
//            }
//        }
//
//        File file = fileRepository.findFileByFileId(attachmentId);
//        if (forumMessage.getFiles().contains(file)) {
//
//        }
//        else{
//            throw new BadRequestException("Сообщение не содержит данного файла");
//        }
//    }

    @Transactional
    public ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        if (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin())) {
            throw new ForbiddenException();
        }
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId());

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        forumMessage.setModifiedTime(LocalDateTime.now());
        forumMessage.setContent(editMessageRequest.getContent());

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно изменено"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteMessage(UserDto user, UUID messageId)
            throws NotFoundException, ForbiddenException{
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        if (user.getRole() != Role.ADMIN) {
            if  (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin()) ||
                    user.getRole() != Role.MODERATOR || user.getManageCategoryId() != forumMessage.getCategoryId()) {
                throw new ForbiddenException();
            }
        }
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId());

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        messageRepository.delete(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно удалено"), HttpStatus.OK);
    }

    public ResponseEntity<MessageResponse> getMessages(UUID themeId, Integer page, Integer size, SortOrder sortOrder)
            throws NotFoundException{
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }

        Sort sort = Sort.by(ComparatorProvider.getComparator(sortOrder));
        Pageable pageable = PageRequest.of(page, size, sort);

        List<MessageDto> messageDtos = messageRepository.findAllByThemeId(themeId, pageable)
                .stream()
                .map(MessageMapper::forumMessageToMessageDto)
                .collect(Collectors.toList());

        Page<MessageDto> messageDtoPage = new PageImpl<>(messageDtos, pageable, messageDtos.size());

        Long totalThemes = messageRepository.countAllByThemeId(themeId);

        Integer totalPages = (int) Math.ceil((double) totalThemes/ size);

        return ResponseEntity.ok(new MessageResponse(
                messageDtoPage.getContent(),
                new PageResponse(
                        totalPages,
                        page,
                        messageDtoPage.getSize(),
                        totalThemes
                )
        ));
    }

    public ResponseEntity<MessageDto> checkMessage(UUID messageId)
            throws NotFoundException{
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null){
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        return ResponseEntity.ok(MessageMapper.forumMessageToMessageDto(forumMessage));
    }

    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(String content,
                                                    LocalDateTime timeFrom,
                                                    LocalDateTime timeTo,
                                                    String authorLogin,
                                                    UUID themeId,
                                                    UUID categoryId){


        List<MessageWithFiltersDto> messages = messageRepository.findAll()
                .stream()
                .filter(m -> content == null || m.getContent().contains(content))
                .filter(m -> timeFrom == null || m.getCreateTime().isAfter(timeFrom))
                .filter(m -> timeTo == null || m.getCreateTime().isBefore(timeTo))
                .filter(m -> authorLogin == null || m.getAuthorLogin().contains(authorLogin))
                .filter(m -> themeId == null || m.getThemeId().equals(themeId))
                .filter(m -> categoryId == null || m.getCategoryId().equals(categoryId))
                .map(MessageMapper::forumMessageToMessageWithFiltersDto)
                .toList();

        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithSubstring(String substring){
        List<MessageWithFiltersDto> forumMessages = messageRepository.findAllByContentContainingIgnoreCase(substring)
                .stream()
                .map(MessageMapper::forumMessageToMessageWithFiltersDto)
                .toList();

        return ResponseEntity.ok(forumMessages);
    }
}
