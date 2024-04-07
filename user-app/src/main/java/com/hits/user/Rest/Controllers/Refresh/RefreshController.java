package com.hits.user.Rest.Controllers.Refresh;

import com.hits.user.Core.RefreshToken.DTO.RefreshRequestDto;
import com.hits.user.Core.RefreshToken.Service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.hits.common.Core.Consts.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@Tag(name = "Refresh токен", description = "Позволяет обновлять access токен с использованием refresh токена")
public class RefreshController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping(REFRESH_TOKEN)
    @Operation(
            summary = "Обновление access токена",
            description = "Позволяет пользователю обновить access токен при помощи refresh токена"
    )
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto){
        return refreshTokenService.refreshJwtToken(refreshRequestDto);
    }
}
