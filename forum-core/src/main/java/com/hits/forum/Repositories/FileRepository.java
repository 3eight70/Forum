package com.hits.forum.Repositories;

import com.hits.forum.Models.Entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileByFileId(UUID fileId);
}
