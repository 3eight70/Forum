package com.hits.file.Models.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "upload_time", nullable = false)
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
    private UUID userId;
}
