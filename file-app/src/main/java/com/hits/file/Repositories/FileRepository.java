package com.hits.file.Repositories;

import com.hits.file.Models.Entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileByIdAndUserId(UUID id, String userId);

    List<File> findAllByUserId(String userId);
}
