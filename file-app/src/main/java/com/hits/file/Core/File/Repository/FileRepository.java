package com.hits.file.Core.File.Repository;

import com.hits.file.Core.File.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileById(UUID id);

    List<File> findAllByMessageId(UUID messageId);
    File findFileByMessageIdAndNameContainsIgnoreCase(UUID messageId, String name);
}
