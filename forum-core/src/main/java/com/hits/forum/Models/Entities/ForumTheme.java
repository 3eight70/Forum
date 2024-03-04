package com.hits.forum.Models.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "themes")
public class ForumTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    @Column(nullable = false)
    private String authorLogin;

    @Column(nullable = false)
    @Size(min = 5, message = "Минимальная длина названия темы равна 5")
    private String themeName;

    @Column(nullable = false)
    private UUID categoryId;

    @OneToMany(mappedBy = "themeId", cascade = CascadeType.ALL)
    private List<ForumMessage> messages;
}
