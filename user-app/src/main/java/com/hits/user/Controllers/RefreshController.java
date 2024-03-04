package com.hits.user.Controllers;

import com.hits.common.Models.Response.Response;
import com.hits.user.Models.Dto.Token.RefreshRequestDto;
import com.hits.user.Services.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.hits.common.Consts.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
public class RefreshController {
    private final IRefreshTokenService refreshTokenService;

    @PostMapping(REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto){
        try{
            return refreshTokenService.refreshJwtToken(refreshRequestDto);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Действие токена истекло"), HttpStatus.UNAUTHORIZED);
        }
    }
}
