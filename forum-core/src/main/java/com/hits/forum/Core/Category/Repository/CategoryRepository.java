package com.hits.forum.Core.Category.Repository;

import com.hits.forum.Core.Category.Entity.ForumCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<ForumCategory, UUID> {
    ForumCategory findByCategoryName(String categoryName);
    ForumCategory findForumCategoryById(UUID id);
    List<ForumCategory> findAllByParentIdIsNull(Sort sort);
    List<ForumCategory> findAllByCategoryNameContainingIgnoreCase(String categoryName);
}
