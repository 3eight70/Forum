package com.hits.forum.Models.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files")
public class File {
    @Id
    private UUID fileId;

    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина названия файла равна 1")
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;
}
