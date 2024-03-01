package com.hits.file.Repositories;

import com.hits.common.Entities.File;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileByIdAndUser(UUID id, UUID userId);

    List<File> findAllByUser(UUID userId);
}
