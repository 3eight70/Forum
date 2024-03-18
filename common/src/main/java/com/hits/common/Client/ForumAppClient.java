package com.hits.common.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
import java.util.List;

import static com.hits.common.Consts.CHECK_THEME;
import static com.hits.common.Consts.GET_THEMES_BY_ID;

@FeignClient(name = "FORUM-SERVICE")
public interface ForumAppClient {
    @GetMapping(CHECK_THEME)
    ResponseEntity<?> checkTheme(@RequestParam(name = "themeId") UUID themeId);

    @GetMapping(GET_THEMES_BY_ID)
    ResponseEntity<?> getThemesById(@RequestParam(name = "themeId") List<UUID> themesId);
}
