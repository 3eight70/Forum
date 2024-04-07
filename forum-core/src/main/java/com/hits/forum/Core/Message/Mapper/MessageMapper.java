package com.hits.forum.Core.Message.Mapper;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.forum.Core.Message.DTO.MessageWithFiltersDto;
import com.hits.forum.Core.Message.Entity.ForumMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class MessageMapper {
    public static ForumMessage messageRequestToForumTheme(String userLogin, String content, UUID themeId, UUID categoryId){
        return new ForumMessage(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                content,
                themeId,
                categoryId,
                new ArrayList<>()
        );
    }

    public static MessageDto forumMessageToMessageDto(ForumMessage forumMessage){
        return new MessageDto(
                forumMessage.getId(),
                forumMessage.getCreateTime(),
                forumMessage.getModifiedTime(),
                forumMessage.getAuthorLogin(),
                forumMessage.getContent(),
                forumMessage.getCategoryId(),
                forumMessage.getThemeId(),
                new ArrayList<>()
        );
    }

    public static MessageWithFiltersDto forumMessageToMessageWithFiltersDto(ForumMessage forumMessage){
        return new MessageWithFiltersDto(
                forumMessage.getId(),
                forumMessage.getCreateTime(),
                forumMessage.getModifiedTime(),
                forumMessage.getCategoryId(),
                forumMessage.getThemeId(),
                forumMessage.getAuthorLogin(),
                forumMessage.getContent(),
                forumMessage.getFiles()
        );
    }
}
