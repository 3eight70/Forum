package com.hits.forum.Core.Category.Service;

import com.hits.common.Core.Category.DTO.CategoryDto;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Category.DTO.CategoryRequest;
import com.hits.forum.Core.Category.DTO.CategoryWithSubstring;
import com.hits.forum.Core.Category.Entity.ForumCategory;
import com.hits.forum.Core.Category.Mapper.CategoryMapper;
import com.hits.forum.Core.Category.Repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseEntity<?> createCategory(UserDto user, CategoryRequest createCategoryRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException {
        String categoryName = createCategoryRequest.getCategoryName();
        categoryRepository.findByCategoryName(categoryName)
                .ifPresent(category -> {
                    throw new ObjectAlreadyExistsException(String.format("Категория с названием=%s уже существует", categoryName));
                });
        ForumCategory forumCategory;

        forumCategory = CategoryMapper.categoryRequestToForumCategory(user.getLogin(), createCategoryRequest);

        UUID parentId = createCategoryRequest.getParentId();
        if (parentId != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(createCategoryRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException(String.format("Категория-родитель с id=%s не существует", parentId)));

            if (!parent.getThemes().isEmpty()){
                throw new BadRequestException("У данной категории уже присутствуют топики");
            }
            parent.getChildCategories().add(forumCategory);
            categoryRepository.saveAndFlush(parent);
        }

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editCategory(UserDto user, UUID categoryId, CategoryRequest createCategoryRequest)
            throws BadRequestException, NotFoundException, ForbiddenException {
        UUID parentId = createCategoryRequest.getParentId();

        if (parentId != null && parentId.equals(categoryId)) {
            throw new BadRequestException("Категория не может быть родителем для самой себя");
        }
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категории с id=%s не существует", categoryId)));

        if (parentId != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(parentId)
                    .orElseThrow(() -> new NotFoundException(String.format("Категория-родитель с id=%s не существует", parentId)));

            if (!parent.getThemes().isEmpty()){
                throw new BadRequestException("У данной категории уже присутствуют топики");
            }
            UUID parentOfParentId = parent.getParentId();
            if (parentOfParentId != null && parentOfParentId.equals(categoryId)) {
                throw new BadRequestException("Категории не могут одновременно быть родителями друг друга");
            }

            parent.getChildCategories().add(forumCategory);
            categoryRepository.saveAndFlush(parent);
        }

        categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName())
                .ifPresent(category -> {
                    if (!Objects.equals(forumCategory.getCategoryName(), category.getCategoryName())) {
                        throw new BadRequestException(String.format("Категория с указанным названием уже существует"));
                    }
                });

        forumCategory.setParentId(parentId);
        forumCategory.setCategoryName(createCategoryRequest.getCategoryName());
        forumCategory.setModifiedTime(LocalDateTime.now());

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно изменена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(UserDto user, UUID categoryId)
            throws NotFoundException, ForbiddenException{
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категории с id=%s не существует",categoryId)));

        categoryRepository.delete(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно удалена"), HttpStatus.OK);
    }

    public ResponseEntity<List<CategoryDto>> getCategories(Sort sortOrder){
        List<CategoryDto> categories = categoryRepository.findAllByParentIdIsNull(sortOrder)
                .stream()
                .map(CategoryMapper::forumCategoryToCategoryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<CategoryDto> checkCategory(UUID categoryId)
            throws NotFoundException{
        ForumCategory forumCategory =  categoryRepository.findForumCategoryById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категории с id=%s не существует", categoryId)));

        return ResponseEntity.ok(CategoryMapper.forumCategoryToCategoryDto(forumCategory));
    }

    public ResponseEntity<List<CategoryWithSubstring>> getCategoriesWithSubstring(String substring){
        List<CategoryWithSubstring> forumCategories = categoryRepository.findAllByCategoryNameContainingIgnoreCase(substring)
                .stream()
                .map(CategoryMapper::forumCategoryToCategoryWithSubstring)
                .toList();

        return ResponseEntity.ok(forumCategories);
    }
}
