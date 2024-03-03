package com.hits.forum.Repositories;

import com.hits.forum.Models.Entities.ForumMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ForumMessage, UUID> {
    ForumMessage findForumMessageById(UUID id);
}
