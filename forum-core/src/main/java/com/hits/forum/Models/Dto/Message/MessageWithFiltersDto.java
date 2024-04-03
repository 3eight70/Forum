package com.hits.forum.Models.Dto.Message;

import com.hits.forum.Models.Entities.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageWithFiltersDto {
    private UUID id;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
    private UUID categoryId;
    private UUID themeId;
    private String authorLogin;
    private String content;
    private List<File> files;
}
