package com.hits.forum.Repositories;

import com.hits.forum.Models.Entities.ForumMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ForumMessage, UUID> {
    ForumMessage findForumMessageById(UUID id);
    List<ForumMessage> findAllByThemeId(UUID themeId, Pageable pageable);
    Long countAllByThemeId(UUID themeId);
    List<ForumMessage> findAllByContentContainingIgnoreCase(String content);
}
