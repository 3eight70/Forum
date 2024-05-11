package com.hits.forum.Core.Theme.Mapper;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.forum.Core.Theme.DTO.ThemeRequest;
import com.hits.forum.Core.Theme.Entity.ForumTheme;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class ThemeMapper {
    public static ForumTheme themeRequestToForumTheme(String userLogin, ThemeRequest themeRequest){
        return new ForumTheme(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                themeRequest.getThemeName(),
                themeRequest.getCategoryId(),
                false,
                new ArrayList<>(),
                new ArrayList<>()
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
