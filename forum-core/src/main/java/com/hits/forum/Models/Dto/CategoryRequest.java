package com.hits.forum.Models.Dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @Size(min = 5, message = "Минимальная длина названия категории равна 5")
    private String categoryName;

    private UUID parentId;
}
