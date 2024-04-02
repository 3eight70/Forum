package com.hits.security.Client;

import com.hits.common.Models.User.UserDto;
import com.hits.security.Configurations.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import static com.hits.common.Consts.*;

@FeignClient(name = "FORUM-SERVICE", configuration = FeignClientConfiguration.class)
public interface ForumAppClient {
    @GetMapping(CHECK_THEME)
    ResponseEntity<?> checkTheme(@RequestParam(name = "themeId") UUID themeId);

    @GetMapping(CHECK_CATEGORY)
    ResponseEntity<?> checkCategory(@RequestParam(name = "categoryId") UUID categoryId);

    @GetMapping(GET_THEMES_BY_ID)
    ResponseEntity<?> getThemesById(@RequestParam(name = "themeId") List<UUID> themesId);
}
