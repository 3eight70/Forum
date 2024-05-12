package com.hits.forum.Core.Theme.Repository;

import com.hits.forum.Core.Theme.Entity.ForumTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThemeRepository extends JpaRepository<ForumTheme, UUID> {
    Optional<ForumTheme> findByThemeNameAndCategoryId(String themeName, UUID categoryId);
    Optional<ForumTheme> findForumThemeById(UUID id);
    List<ForumTheme> findAllByThemeNameContainingIgnoreCase(String themeName);
}
