package com.hits.security.Rest.Client;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.security.Rest.Configurations.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@FeignClient(name = "FORUM-SERVICE", configuration = FeignClientConfiguration.class)
public interface ForumAppClient {
    @GetMapping(CHECK_THEME)
    ResponseEntity<?> checkTheme(@RequestParam(name = "themeId") UUID themeId);

    @GetMapping(CHECK_CATEGORY)
    ResponseEntity<?> checkCategory(@RequestParam(name = "categoryId") UUID categoryId);

    @GetMapping(SEND_MESSAGE)
    ResponseEntity<MessageDto> checkMessage(@RequestParam(name = "messageId") UUID messageId);

    @GetMapping(GET_THEMES_BY_ID)
    ResponseEntity<List<ThemeDto>> getThemesById(@RequestParam(name = "themeId") List<UUID> themesId);
}
