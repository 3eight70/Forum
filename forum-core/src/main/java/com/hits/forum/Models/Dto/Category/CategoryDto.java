package com.hits.forum.Models.Dto.Category;

import com.hits.forum.Models.Entities.ForumCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private UUID id;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;
    private String name;
    private UUID parentId;
    private List<CategoryDto> childCategories;
}
