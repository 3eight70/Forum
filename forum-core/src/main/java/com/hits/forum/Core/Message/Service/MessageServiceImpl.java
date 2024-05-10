package com.hits.forum.Core.Message.Service;

import com.hits.common.Core.File.DTO.FileDto;
import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.File.Entity.File;
import com.hits.forum.Core.File.Mapper.FileMapper;
import com.hits.forum.Core.File.Repository.FileRepository;
import com.hits.forum.Core.Message.DTO.EditMessageRequest;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersRequest;
import com.hits.forum.Core.Message.Entity.ForumMessage;
import com.hits.forum.Core.Message.Mapper.MessageMapper;
import com.hits.forum.Core.Message.Repository.MessageRepository;
import com.hits.forum.Core.Message.Specifications.MessageSpecification;
import com.hits.forum.Core.Theme.Entity.ForumTheme;
import com.hits.forum.Core.Theme.Repository.ThemeRepository;
import com.hits.security.Rest.Client.FileAppClient;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{
    private final ThemeRepository themeRepository;
    private final MessageRepository messageRepository;
    private final FileRepository fileRepository;
    private final FileAppClient fileAppClient;

    @Transactional
    public ResponseEntity<?> createMessage(UserDto user, String content, UUID themeId)
            throws NotFoundException, BadRequestException {

        if (content.isEmpty()){
            throw new BadRequestException("Минимальная длина сообщения равна 1");
        }

        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Тема-родитель с id=%s не существует", themeId)));

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        ForumMessage forumMessage = MessageMapper.messageRequestToForumTheme(user.getLogin(), content,
                themeId, forumTheme.getCategoryId());

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно создано"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteAttachmentFromMessage(UserDto user, UUID attachmentId, UUID messageId)
            throws IOException, NotFoundException {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId)
                .orElseThrow(() -> new NotFoundException(String.format("Сообщения с id=%s не существует", messageId)));

        checkAccess(user, forumMessage);

        File file = fileRepository.findFileByFileId(attachmentId).
                orElseThrow(() -> new NotFoundException(String.format("Файл с id=%s не найден", attachmentId)));
        List<File> files = forumMessage.getFiles();

        if (files.contains(file)) {
            files.remove(file);
            fileRepository.delete(file);
            messageRepository.saveAndFlush(forumMessage);

            try{
                fileAppClient.deleteFile(user, forumMessage.getId(), file.getFileId());
            }
            catch (FeignException.NotFound e){
                throw new NotFoundException("Файл с заданным id не найден");
            }

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Вложение успешно удалено"), HttpStatus.OK);
        }
        else{
            throw new BadRequestException("Сообщение не содержит данного файла");
        }
    }

    @Transactional
    public ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId)
                .orElseThrow(() -> new NotFoundException(String.format("Сообщения с id=%s не существует", messageId)));

        if (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin())) {
            throw new ForbiddenException();
        }
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId()).get();

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
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId)
                .orElseThrow(() -> new NotFoundException(String.format("Сообщения с id=%s не существует", messageId)));

        checkAccess(user, forumMessage);
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId()).get();

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        messageRepository.delete(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно удалено"), HttpStatus.OK);
    }

    public ResponseEntity<Page<MessageDto>> getMessages(UUID themeId, Pageable pageable) {
        return ResponseEntity.ok(new PageImpl<>(messageRepository.findAllByThemeId(themeId, pageable)
                .stream()
                .map(MessageMapper::forumMessageToMessageDto)
                .collect(Collectors.toList())));
    }

    public ResponseEntity<MessageDto> checkMessage(UUID messageId)
            throws NotFoundException{
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId)
                .orElseThrow(() -> new NotFoundException(String.format("Сообщения с id=%s не существует", messageId)));

        return ResponseEntity.ok(MessageMapper.forumMessageToMessageDto(forumMessage));
    }

    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(MessageWithFiltersRequest request){
        Specification<ForumMessage> specification = Specification
                .where(MessageSpecification.contentLike(request.getContent()))
                .and(MessageSpecification.themeIdEquals(request.getThemeId()))
                .and(MessageSpecification.authorLoginLike(request.getAuthorLogin()))
                .and(MessageSpecification.categoryIdEquals(request.getCategoryId()))
                .and(MessageSpecification.timeGreaterOrEqualThan(request.getTimeFrom()))
                .and(MessageSpecification.timeLessOrEqualThan(request.getTimeTo()));

        List<MessageWithFiltersDto> list = messageRepository.findAll(specification)
                .stream()
                .map(MessageMapper::forumMessageToMessageWithFiltersDto)
                .toList();

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithSubstring(String substring){
        List<MessageWithFiltersDto> forumMessages = messageRepository.findAllByContentContainingIgnoreCase(substring)
                .stream()
                .map(MessageMapper::forumMessageToMessageWithFiltersDto)
                .toList();

        return ResponseEntity.ok(forumMessages);
    }

    @Transactional
    public ResponseEntity<?> attachFilesToMessage(UserDto user, List<UUID> files, UUID messageId)
            throws NotFoundException{
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId)
                .orElseThrow(() -> new NotFoundException(String.format("Сообщение с id=%s не найдено", messageId)));

        if (!forumMessage.getAuthorLogin().equals(user.getLogin())){
            throw new ForbiddenException();
        }

        try {
            List<File> attachments = new ArrayList<>();
            List<UUID> attachmentIds = forumMessage.getFiles()
                    .stream()
                    .map(File::getFileId)
                    .toList();

            for (UUID id : files){
                FileDto fileDto = fileAppClient.checkFile(id).getBody();
                File file = FileMapper.fileDtoToFile(fileDto);

                if (attachmentIds.contains(id)){
                    throw new BadRequestException("Файл уже прикреплен к сообщению");
                }

                attachments.add(file);

                fileRepository.saveAndFlush(file);
            }

            forumMessage.getFiles().addAll(attachments);
            messageRepository.saveAndFlush(forumMessage);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Вложения прикреплены"), HttpStatus.OK);
        }
        catch (FeignException.BadRequest e){
            throw new BadRequestException("Вы уже загрузили файл с таким же названием");
        }
        catch (FeignException.NotFound e){
            throw new NotFoundException("Файл не найден");
        }
    }

    private void checkAccess(UserDto user, ForumMessage forumMessage){
        if (user.getRole() != Role.ADMIN) {
            if  (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin()) &&
                    (user.getRole() != Role.MODERATOR || !user.getManageCategoryId().contains(forumMessage.getCategoryId()) )) {
                throw new ForbiddenException();
            }
        }
    }
}