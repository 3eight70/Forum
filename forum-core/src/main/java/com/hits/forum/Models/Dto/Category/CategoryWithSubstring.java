package com.hits.forum.Models.Dto.Category;

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
