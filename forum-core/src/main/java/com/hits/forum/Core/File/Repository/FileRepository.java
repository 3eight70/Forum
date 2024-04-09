package com.hits.forum.Core.File.Repository;

import com.hits.forum.Core.File.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findFileByFileId(UUID fileId);
}
