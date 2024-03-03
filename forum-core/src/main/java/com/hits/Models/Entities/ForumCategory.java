package com.hits.Models.Entities;

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
@Table(name = "categories")
public class ForumCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    @Column(nullable = false)
    private String authorLogin;

    @Column(nullable = false)
    @Size(min = 5, message = "Минимальная длина названия категории равна 5")
    private String categoryName;

    private UUID parentId;
}
