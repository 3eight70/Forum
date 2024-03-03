package com.hits.forum.Models.Dto.Responses;

import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponse {
    private List<ThemeRequest> themes;
    private PageResponse page;
}
