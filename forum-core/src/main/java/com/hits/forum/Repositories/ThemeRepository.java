package com.hits.forum.Repositories;

import com.hits.forum.Models.Entities.ForumTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThemeRepository extends JpaRepository<ForumTheme, UUID> {
    ForumTheme findByThemeNameAndCategoryId(String themeName, UUID categoryId);
    ForumTheme findForumThemeById(UUID id);
    List<ForumTheme> findAllByThemeNameContainingIgnoreCase(String themeName);

    List<ForumTheme> findAllByIdIn(List<UUID> themeIds);
}
