package com.hits.forum.Core.Utils;

import com.hits.forum.Core.Enums.SortOrder;
import org.springframework.data.domain.Sort;

public class ComparatorProvider {
    public static Sort.Order getComparator(SortOrder sortOrder) {
        return switch (sortOrder) {
            case CreateDesc -> Sort.Order.desc("createTime");
            case CreateAsc -> Sort.Order.asc("createTime");
            case NameAsc -> Sort.Order.asc("categoryName");
            case NameDesc -> Sort.Order.desc("categoryName");
        };
    }
}
