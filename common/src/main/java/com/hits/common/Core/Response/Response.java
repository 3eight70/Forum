package com.hits.common.Core.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Ответ на запросы")
public class Response {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Время получения ответа")
    private final LocalDateTime time = LocalDateTime.now();

    @Schema(description = "Статус ответа")
    private final int status;

    @Schema(description = "Сообщение ответа")
    private final String message;

    public Response(int value, String message) {
        this.status = value;
        this.message = message;
    }
}
