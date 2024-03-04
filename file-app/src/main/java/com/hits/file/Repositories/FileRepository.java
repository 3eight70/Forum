package com.hits.file.Repositories;

<<<<<<< HEAD
import com.hits.file.Models.Entity.File;
=======
import com.hits.file.Models.Entities.File;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
<<<<<<< HEAD
    File findFileByIdAndUser(UUID id, UUID userId);

    List<File> findAllByUser(UUID userId);
=======
    File findFileByIdAndUserId(UUID id, String userId);

    List<File> findAllByUserId(String userId);
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
}
