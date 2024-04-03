package com.hits.common.Models.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private UUID id;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    private String authorLogin;

    private String content;

    private UUID categoryId;

    private UUID themeId;

    private List<UUID> files;
}

