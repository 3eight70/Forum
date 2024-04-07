package com.hits.forum.Core.Category.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithSubstring {
    private UUID id;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
    private String name;
    private UUID parentId;
}
