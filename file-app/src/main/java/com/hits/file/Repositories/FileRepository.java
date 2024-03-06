package com.hits.file.Repositories;

import com.hits.file.Models.Entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileByIdAndUserId(UUID id, UUID userId);

    List<File> findAllByUserId(UUID userId);
}
