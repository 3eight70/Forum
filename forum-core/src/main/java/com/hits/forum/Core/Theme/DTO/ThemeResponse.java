package com.hits.forum.Core.Theme.DTO;

import com.hits.common.Core.Page.DTO.PageResponse;
import com.hits.common.Core.Theme.DTO.ThemeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с темами")
public class ThemeResponse {
    @Schema(description = "Список тем")
    private List<ThemeDto> themes;

    @Schema(description = "Пагинация")
    private PageResponse page;
}
