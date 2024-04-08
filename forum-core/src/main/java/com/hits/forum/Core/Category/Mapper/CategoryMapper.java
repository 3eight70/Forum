package com.hits.forum.Core.Category.Mapper;

import com.hits.forum.Core.Category.DTO.CategoryDto;
import com.hits.forum.Core.Category.DTO.CategoryRequest;
import com.hits.forum.Core.Category.DTO.CategoryWithSubstring;
import com.hits.forum.Core.Category.Entity.ForumCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CategoryMapper {
    public static ForumCategory categoryRequestToForumCategory(String userLogin, CategoryRequest categoryRequest){
        return new ForumCategory(
                UUID.randomUUID(),
                LocalDateTime.now(),
                null,
                userLogin,
                categoryRequest.getCategoryName(),
                categoryRequest.getParentId(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static CategoryDto forumCategoryToCategoryDto(ForumCategory forumCategory){
        return new CategoryDto(
                forumCategory.getId(),
                forumCategory.getCreateTime(),
                forumCategory.getModifiedTime(),
                forumCategory.getCategoryName(),
                forumCategory.getParentId(),
                forumCategory.getChildCategories()
                        .stream()
                        .map(CategoryMapper::forumCategoryToCategoryDto)
                        .collect(Collectors.toList())
        );
    }

    public static CategoryWithSubstring forumCategoryToCategoryWithSubstring(ForumCategory forumCategory){
        return new CategoryWithSubstring(
                forumCategory.getId(),
                forumCategory.getCreateTime(),
                forumCategory.getModifiedTime(),
                forumCategory.getCategoryName(),
                forumCategory.getParentId()
        );
    }


}
