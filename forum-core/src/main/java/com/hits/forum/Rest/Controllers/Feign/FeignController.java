package com.hits.forum.Rest.Controllers.Feign;

import com.hits.common.Core.Category.DTO.CategoryDto;
import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Category.Service.CategoryService;
import com.hits.forum.Core.Message.Service.MessageService;
import com.hits.forum.Core.Theme.Service.ThemeService;
import com.hits.security.Rest.Client.ForumAppClient;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.GET_THEMES_BY_ID;
import static com.hits.common.Core.Consts.SEND_MESSAGE;

@RestController
@RequiredArgsConstructor
@Hidden
public class FeignController implements ForumAppClient {
    private final CategoryService categoryService;
    private final ThemeService themeService;
    private final MessageService messageService;


    @Override
    public ResponseEntity<?> checkTheme(@RequestParam(name = "themeId") UUID themeId)
            throws NotFoundException{
        return themeService.checkTheme(themeId);
    }

    @Override
    public ResponseEntity<CategoryDto> checkCategory(@RequestParam(name = "categoryId") UUID categoryId)
            throws NotFoundException{
        return categoryService.checkCategory(categoryId);
    }

    @GetMapping(GET_THEMES_BY_ID)
    public ResponseEntity<List<ThemeDto>> getThemesById(@RequestParam(name = "themeId") List<UUID> themesId){
        return themeService.getThemesById(themesId);
    }

    @GetMapping(SEND_MESSAGE)
    public ResponseEntity<MessageDto> checkMessage(
            @RequestParam(name = "messageId") UUID messageId) {
        return messageService.checkMessage(messageId);
    }
}
