package com.hits.forum.Models.Dto.Responses;

import com.hits.common.Models.Theme.ThemeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponse {
    private List<ThemeDto> themes;
    private PageResponse page;
}
