package com.hits.forum.Mappers;

import com.hits.common.Models.Theme.ThemeDto;
import com.hits.forum.Models.Dto.Category.CategoryDto;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Category.CategoryWithSubstring;
import com.hits.forum.Models.Dto.Message.MessageDto;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Message.MessageWithFiltersDto;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Entities.ForumCategory;
import com.hits.forum.Models.Entities.ForumMessage;
import com.hits.forum.Models.Entities.ForumTheme;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class ForumMapper {
    public static ForumCategory categoryRequestToForumCategory(String userLogin, CategoryRequest categoryRequest){
        return new ForumCategory(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                categoryRequest.getCategoryName(),
                categoryRequest.getParentId(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static CategoryDto forumCategoryToCategoryDto(ForumCategory forumCategory){
        return new CategoryDto(
                forumCategory.getId(),
                forumCategory.getCreateTime(),
                forumCategory.getModifiedTime(),
                forumCategory.getCategoryName(),
                forumCategory.getParentId(),
                forumCategory.getChildCategories()
                        .stream()
                        .map(ForumMapper::forumCategoryToCategoryDto)
                        .collect(Collectors.toList())
        );
    }

    public static CategoryWithSubstring forumCategoryToCategoryWithSubstring(ForumCategory forumCategory){
        return new CategoryWithSubstring(
                forumCategory.getId(),
                forumCategory.getCreateTime(),
                forumCategory.getModifiedTime(),
                forumCategory.getCategoryName(),
                forumCategory.getParentId()
        );
    }

    public static MessageDto forumMessageToMessageDto(ForumMessage forumMessage){
        return new MessageDto(
                forumMessage.getId(),
                forumMessage.getCreateTime(),
                forumMessage.getModifiedTime(),
                forumMessage.getAuthorLogin(),
                forumMessage.getContent()
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
                forumMessage.getContent()
        );
    }

    public static ForumTheme themeRequestToForumTheme(String userLogin, ThemeRequest themeRequest){
        return new ForumTheme(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                themeRequest.getThemeName(),
                themeRequest.getCategoryId(),
                new ArrayList<>()
        );
    }

    public static ForumMessage messageRequestToForumTheme(String userLogin, MessageRequest messageRequest, UUID categoryId){
        return new ForumMessage(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                messageRequest.getContent(),
                messageRequest.getThemeId(),
                categoryId
        );
    }

    public static ThemeDto forumThemeToThemeDto(ForumTheme forumTheme){
        return new ThemeDto(
                forumTheme.getId(),
                forumTheme.getCreateTime(),
                forumTheme.getModifiedTime(),
                forumTheme.getThemeName(),
                forumTheme.getCategoryId()
        );
    }
}
