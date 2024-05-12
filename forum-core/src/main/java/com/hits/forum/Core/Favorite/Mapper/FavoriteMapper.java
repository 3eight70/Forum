package com.hits.forum.Core.Favorite.Mapper;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.forum.Core.Favorite.Entity.Favorite;
import com.hits.forum.Core.Theme.Entity.ForumTheme;

public final class FavoriteMapper {
    public static ThemeDto favoriteToThemeDto(Favorite favorite) {
        ForumTheme forumTheme = favorite.getTheme();

        return new ThemeDto(
                forumTheme.getId(),
                forumTheme.getCreateTime(),
                forumTheme.getModifiedTime(),
                forumTheme.getThemeName(),
                forumTheme.getCategoryId()
        );
    }
}
