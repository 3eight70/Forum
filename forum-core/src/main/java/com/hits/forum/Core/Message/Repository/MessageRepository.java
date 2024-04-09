package com.hits.forum.Core.Message.Repository;

import com.hits.forum.Core.Message.Entity.ForumMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ForumMessage, UUID>, JpaSpecificationExecutor<ForumMessage> {
    Optional<ForumMessage> findForumMessageById(UUID id);
    Page<ForumMessage> findAllByThemeId(UUID themeId, Pageable pageable);
    Long countAllByThemeId(UUID themeId);
    List<ForumMessage> findAllByContentContainingIgnoreCase(String content);
}
