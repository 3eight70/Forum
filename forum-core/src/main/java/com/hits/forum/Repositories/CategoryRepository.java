package com.hits.forum.Repositories;

import com.hits.forum.Models.Entities.ForumCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<ForumCategory, UUID> {
    ForumCategory findByCategoryName(String categoryName);

    ForumCategory findForumCategoryById(UUID id);
}
