package com.hits.forum.Core.Favorite.Service;

import com.hits.common.Core.Response.Response;
import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Favorite.Entity.Favorite;
import com.hits.forum.Core.Favorite.Mapper.FavoriteMapper;
import com.hits.forum.Core.Favorite.Repository.FavoriteRepository;
import com.hits.forum.Core.Theme.Entity.ForumTheme;
import com.hits.forum.Core.Theme.Repository.ThemeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId) throws NotFoundException {
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        UUID userId = userDto.getId();
        favoriteRepository.findFavoriteByThemeIdAndUserId(themeId, userId)
                .ifPresent((favorite) -> {
                    throw new BadRequestException(String.format("Тема с id=%s и так находится в избранном пользователя", themeId));
                });

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTheme(forumTheme);

        favoriteRepository.save(favorite);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно добавил тему в избранное"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId) throws NotFoundException{
        themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        Favorite favorite = favoriteRepository.findFavoriteByThemeIdAndUserId(themeId, userDto.getId())
                .orElseThrow(() -> new BadRequestException(String.format("Тема с id=%s не находится в избранном пользователя", themeId)));

        favoriteRepository.delete(favorite);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно удалил тему из избранного"), HttpStatus.OK);
    }

    public ResponseEntity<Page<ThemeDto>> getFavoriteThemes(UserDto userDto, Pageable pageable){
        List<ThemeDto> themes = favoriteRepository.findAllByUserId(userDto.getId(), pageable)
                .stream()
                .map(FavoriteMapper::favoriteToThemeDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(themes));
    }
}
