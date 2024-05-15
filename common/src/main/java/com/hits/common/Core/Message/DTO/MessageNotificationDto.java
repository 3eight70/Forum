package com.hits.common.Core.Message.DTO;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель для уведомлений при отправке сообщения")
public class MessageNotificationDto {
    @Schema(description = "Идентификатор пользователя, кому отправляется уведомление")
    private UUID userId;

    @Schema(description = "Заголовок")
    private String title;

    @Schema(description = "Содержание уведомления")
    private String content;

    @Schema(description = "Каналы, по которым кидать уведомление")
    List<NotificationChannel> channels;

    @Schema(description = "Нужно ли сохранить в историю")
    private Boolean needInHistory;
}
