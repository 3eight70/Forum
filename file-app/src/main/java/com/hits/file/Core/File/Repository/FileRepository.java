package com.hits.file.Core.File.Repository;

import com.hits.file.Core.File.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findFileById(UUID id);

    List<File> findAllByAuthorLogin(String authorLogin);
    File findFileByAuthorLoginAndNameContainsIgnoreCase(String authorLogin, String name);
}
