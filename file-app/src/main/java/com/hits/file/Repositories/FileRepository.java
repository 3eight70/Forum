package com.hits.file.Repositories;

import com.hits.file.Models.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileByIdAndUser(UUID id, UUID userId);

    List<File> findAllByUser(UUID userId);
}
