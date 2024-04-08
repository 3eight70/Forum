package com.hits.forum.Core.Message.Service;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.*;
import com.hits.forum.Core.Category.Repository.CategoryRepository;
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
import org.springframework.web.multipart.MultipartFile;

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

        List<File> filesToDatabase = mapMultipartList(files);

        fileRepository.saveAll(filesToDatabase);

        forumMessage.setFiles(filesToDatabase);
        messageRepository.saveAndFlush(forumMessage);

        uploadFiles(files, filesToDatabase, forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно создано"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteAttachmentFromMessage(UserDto user, UUID attachmentId, UUID messageId)
            throws IOException, NotFoundException {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        checkAccess(user, forumMessage);

        File file = fileRepository.findFileByFileId(attachmentId);
        if (forumMessage.getFiles().contains(file)) {
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
    public ResponseEntity<?> editMessage(UserDto user, UUID messageId, List<MultipartFile> files, EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException, IOException {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        if (files != null && forumMessage.getFiles().size() + files.size() > 5){
            throw new FileLimitException();
        }

        if (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin())) {
            throw new ForbiddenException();
        }
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId());

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        List<File> filesToDatabase = mapMultipartList(files);;

        forumMessage.setModifiedTime(LocalDateTime.now());
        forumMessage.setContent(editMessageRequest.getContent());
        forumMessage.getFiles().addAll(filesToDatabase);

        messageRepository.saveAndFlush(forumMessage);

        uploadFiles(files, filesToDatabase, forumMessage);

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

        checkAccess(user, forumMessage);
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId());

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
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null){
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        return ResponseEntity.ok(MessageMapper.forumMessageToMessageDto(forumMessage));
    }

    public ResponseEntity<List<MessageWithFiltersDto>> getMessagesWithFilters(MessageWithFiltersRequest request){
        Specification<ForumMessage> specification = Specification
                .where(MessageSpecification.contentLike(request.getContent()))
                .and(MessageSpecification.themeIdEquals(request.getThemeId()))
                .and(MessageSpecification.authorLoginLike(request.getAuthorLogin()))
                .and(MessageSpecification.categoryIdEquals(request.getCategoryId()))
                .and(MessageSpecification.timeBetween(request.getTimeFrom(), request.getTimeTo()));

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

    private List<File> mapMultipartList(List<MultipartFile> files){
        List<File> filesToDatabase = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            filesToDatabase = files
                    .stream()
                    .map(FileMapper::multipartFileToFile)
                    .toList();
        }

        return filesToDatabase;
    }

    private void uploadFiles(List<MultipartFile> files, List<File> filesToDatabase, ForumMessage forumMessage) throws IOException{
        try {
            if (files != null && !files.isEmpty()) {
                for (int i = 0; i < files.size(); i++) {
                    MultipartFile file = files.get(i);
                    File fileFromDatabase = filesToDatabase.get(i);
                    fileAppClient.uploadFile(forumMessage.getId().toString(), fileFromDatabase.getFileId().toString(), file);
                }
            }
        }
        catch (FeignException.BadRequest e){
            throw new BadRequestException("Вы уже загрузили файл с таким же названием");
        }
    }

    private void checkAccess(UserDto user, ForumMessage forumMessage){
        if (user.getRole() != Role.ADMIN) {
            System.out.println(Objects.equals(user.getLogin(), forumMessage.getAuthorLogin()));
            if  (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin()) &&
                    (user.getRole() != Role.MODERATOR || user.getManageCategoryId() != forumMessage.getCategoryId())) {
                throw new ForbiddenException();
            }
        }
    }
}