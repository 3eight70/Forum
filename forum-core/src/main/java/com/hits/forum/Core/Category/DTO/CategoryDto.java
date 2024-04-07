package com.hits.forum.Core.Category.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель категории")
public class CategoryDto {
    @Schema(description = "Идентификатор категории")
    private UUID id;

    @Schema(description = "Время создания категории")
    private LocalDateTime createTime;

    @Schema(description = "Время изменения категории")
    private LocalDateTime modifiedTime;

    @Schema(description = "Название категории")
    private String name;

    @Schema(description = "Идентификатор категории-родителя")
    private UUID parentId;

    @Schema(description = "Список категорий-детей")
    private List<CategoryDto> childCategories;
}
