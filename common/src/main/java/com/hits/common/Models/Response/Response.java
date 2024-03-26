package com.hits.common.Models.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Response {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime time = LocalDateTime.now();
    private final int status;
    private final String message;

    public Response(int value, String message) {
        this.status = value;
        this.message = message;
    }
}
