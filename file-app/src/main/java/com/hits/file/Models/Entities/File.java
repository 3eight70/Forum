<<<<<<<< HEAD:file-app/src/main/java/com/hits/file/Models/Entity/File.java
package com.hits.file.Models.Entity;
========
package com.hits.file.Models.Entities;
>>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d:file-app/src/main/java/com/hits/file/Models/Entities/File.java

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {
    @Id
    private UUID id;

    @Column(name = "download_time", nullable = false)
    private LocalDateTime uploadTime = LocalDateTime.now();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content_type", nullable = false)
    @Size(min = 1, max = 1000)
    private String contentType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "file_content", nullable = false)
    private byte[] fileContent;

    @Column(name = "user_id", nullable = false)
<<<<<<<< HEAD:file-app/src/main/java/com/hits/file/Models/Entity/File.java
    private UUID user;
========
    private String userId;
>>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d:file-app/src/main/java/com/hits/file/Models/Entities/File.java
}
