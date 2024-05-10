package com.hits.notification.Core.Notification.Entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name="notifications")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность уведомлений")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Идентификатор уведомления")
    private UUID id;

    @Column(name = "title")
    @Schema(description = "Заголовок")
    private String title;

    @Column(name = "content")
    @Schema(description = "Содержание уведомления")
    private String content;

    @Column(name = "user_login")
    @Schema(description = "Логин получателя")
    private String userLogin;

    @Column(name = "create_time")
    @Schema(description = "Время создания")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "is_read")
    @Schema(description = "Статус прочтения")
    private Boolean isRead = false;
}
