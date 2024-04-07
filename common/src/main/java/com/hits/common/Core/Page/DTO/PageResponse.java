package com.hits.common.Core.Page.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse {
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private long totalElements;
}
