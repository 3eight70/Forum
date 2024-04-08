package com.hits.forum.Core.Category.Service;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Category.DTO.CategoryDto;
import com.hits.forum.Core.Category.DTO.CategoryRequest;
import com.hits.forum.Core.Category.DTO.CategoryWithSubstring;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    ResponseEntity<?> createCategory(UserDto user, CategoryRequest createCategoryRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException;
    ResponseEntity<?> editCategory(UserDto user, UUID categoryId, CategoryRequest createCategoryRequest)
            throws BadRequestException, NotFoundException, ForbiddenException;
    ResponseEntity<?> deleteCategory(UserDto user, UUID categoryId)
            throws NotFoundException, ForbiddenException;
    ResponseEntity<List<CategoryDto>>  getCategories(Sort sortOrder);
    ResponseEntity<List<CategoryWithSubstring>>  getCategoriesWithSubstring(String substring);
    ResponseEntity<?> checkCategory(UUID categoryId) throws NotFoundException;
}
