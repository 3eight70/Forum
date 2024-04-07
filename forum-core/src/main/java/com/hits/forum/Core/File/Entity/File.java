package com.hits.forum.Core.File.Entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Сущность файла")
public class File {
    @Id
    @Schema(description = "Идентификатор в хранилище")
    private UUID fileId;

    @Column(nullable = false)
    @Size(min = 1, message = "Минимальная длина названия файла равна 1")
    @Schema(description = "Название файла")
    private String fileName;

    @Column(nullable = false)
    @Schema(description = "Размер файла")
    private Long fileSize;
}
