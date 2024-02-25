package com.hits.user.Controllers;

import com.hits.user.Models.Dto.Response.Response;
import com.hits.user.Models.Dto.Response.TokenResponse;
import com.hits.user.Models.Dto.Token.RefreshRequestDto;
import com.hits.user.Models.Entity.RefreshToken;
import com.hits.user.Repositories.RefreshRepository;
import com.hits.user.Services.IRefreshTokenService;
import com.hits.user.Utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RefreshController {
    private final RefreshRepository refreshRepository;
    private final IRefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto){
        try{
            return refreshRepository.findByToken(refreshRequestDto.getToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String accessToken = jwtTokenUtils.generateToken(user);
                        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(accessToken), "Valid");

                        return ResponseEntity.ok(new TokenResponse(accessToken, refreshRequestDto.getToken()));
                    })
                    .orElseThrow(() -> new RuntimeException("Данного токена нет в базе данных"));
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Действие токена истекло"), HttpStatus.UNAUTHORIZED);
        }
    }
}
