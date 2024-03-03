package com.hits.forum.Mappers;

import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Entities.ForumCategory;
import com.hits.forum.Models.Entities.ForumMessage;
import com.hits.forum.Models.Entities.ForumTheme;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class ForumMapper {
    public static ForumCategory categoryRequestToForumCategory(String userLogin, CategoryRequest categoryRequest){
        return new ForumCategory(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                categoryRequest.getCategoryName(),
                categoryRequest.getParentId(),
                new ArrayList<>()
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

    public static ForumMessage messageRequestToForumTheme(String userLogin, MessageRequest messageRequest){
        return new ForumMessage(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                messageRequest.getContent(),
                messageRequest.getThemeId()
        );
    }

    public static ThemeRequest forumThemeToThemeRequest(ForumTheme forumTheme){
        return new ThemeRequest(
                forumTheme.getThemeName(),
                forumTheme.getCategoryId()
        );
    }
}
