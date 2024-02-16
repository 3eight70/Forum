package com.hits.FileSystem.Repositories;

import com.hits.FileSystem.Models.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    File findFileById(UUID id);
}
