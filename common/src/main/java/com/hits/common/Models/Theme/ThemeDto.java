package com.hits.common.Models.Theme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemeDto {
    private UUID themeId;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
    private String themeName;
    private UUID categoryId;
}
