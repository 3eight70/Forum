package com.hits.common.Core.Notification.DTO;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Schema(description = "Модель получателя")
    @NotNull
    private UserNotificationDTO userNotification;

    @Schema(description = "Время создания")
    private LocalDateTime createTime;

    @Schema(description = "Каналы отправки")
    private List<NotificationChannel> channelList;

    @Schema(description = "Флаг, отображающий, нужно ли сохранять в истории")
    private Boolean needInHistory;
}

