package com.hits.forum.Core.Message.Specifications;

import com.hits.forum.Core.Message.Entity.ForumMessage;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public final class MessageSpecification {
    public static Specification<ForumMessage> contentLike(String content){
        if (content == null || content.isEmpty()){
            return null;
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("content"), "%" + content + "%"));
    }

    public static Specification<ForumMessage> timeBetween(LocalDateTime timeFrom, LocalDateTime timeTo){
        if (timeFrom != null && timeTo != null) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createTime"), timeFrom, timeTo));
        }
        else if (timeFrom != null){
            return (((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), timeFrom)));
        }
        else if (timeTo != null){
            return (((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), timeTo)));
        }

        return null;
    }

    public static Specification<ForumMessage> authorLoginLike(String authorLogin){
        if (authorLogin == null || authorLogin.isEmpty()){
            return null;
        }

        return (((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("authorLogin"), "%" + authorLogin + "%")));
    }

    public static Specification<ForumMessage> themeIdEquals(UUID themeId){
        if (themeId == null){
            return null;
        }

        return (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("themeId"), themeId)));
    }

    public static Specification<ForumMessage> categoryIdEquals(UUID categoryId){
        if (categoryId == null){
            return null;
        }

        return (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoryId"), categoryId)));
    }
}
