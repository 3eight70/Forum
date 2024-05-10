package com.hits.forum.Rest.Controllers.Category;

import com.hits.common.Core.Category.DTO.CategoryDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Category.DTO.CategoryRequest;
import com.hits.forum.Core.Category.DTO.CategoryWithSubstring;
import com.hits.forum.Core.Category.Service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Категории", description = "Отвечает за работу с категориями")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping(GET_CATEGORIES)
    @Operation(
            summary = "Получение категорий",
            description = "Позволяет получить категории, отсортированные в нужном порядке"
    )
    public ResponseEntity<List<CategoryDto>> getCategories(
            @SortDefault(sort = "categoryName", direction = Sort.Direction.ASC) @RequestParam(name = "sortOrder", required = false) @Parameter(description = "Сортировка") Sort sortOrder){
        return categoryService.getCategories(sortOrder);
    }

    @PostMapping(CREATE_CATEGORY)
    @Operation(
            summary = "Создание категории",
            description = "Позволяет создать категорию"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createCategory(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody CategoryRequest createCategoryRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException {
        return categoryService.createCategory(user, createCategoryRequest);
    }

    @PutMapping(EDIT_CATEGORY)
    @Operation(
            summary = "Изменение категории",
            description = "Позволяет изменить категорию"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> editCategory(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "categoryId") UUID categoryId,
            @Valid @RequestBody CategoryRequest createCategoryRequest)
            throws BadRequestException, NotFoundException, ForbiddenException {
        return categoryService.editCategory(user, categoryId, createCategoryRequest);
    }

    @DeleteMapping(DELETE_CATEGORY)
    @Operation(
            summary = "Удаление категории",
            description = "Позволяет удалить категорию"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteCategory(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "categoryId") @Parameter(description = "Идентификатор категории") UUID categoryId)
            throws NotFoundException, ForbiddenException{
        return categoryService.deleteCategory(user, categoryId);
    }

    @GetMapping(GET_CATEGORIES_WITH_SUBSTRING)
    @Operation(
            summary = "Поиск категорий по подстроке",
            description = "Позволяет получить список категорий, используя поиск по подстроке"
    )
    public ResponseEntity<List<CategoryWithSubstring>> getCategoriesWithSubstring(@RequestParam(value = "name") @Parameter(description = "Название категории") String name){
        return categoryService.getCategoriesWithSubstring(name);
    }
}
