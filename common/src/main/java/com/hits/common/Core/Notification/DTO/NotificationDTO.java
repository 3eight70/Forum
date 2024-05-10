package com.hits.common.Core.Notification.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель для уведомлений")
public class NotificationDTO {
    @Schema(description = "Заголовок")
    @NotNull
    private String title;

    @Schema(description = "Содержание уведомления")
    @NotNull
    private String content;

    @Schema(description = "Логин получателя")
    @NotNull
    private String userLogin;

    @Schema(description = "Время создания")
    private LocalDateTime createTime;
}

