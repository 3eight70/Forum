package com.hits.user.Services;

import com.hits.common.Models.Response.TokenResponse;
import com.hits.user.Models.Dto.Token.RefreshRequestDto;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
import com.hits.user.Repositories.RefreshRepository;
import com.hits.user.Repositories.UserRepository;
import com.hits.user.Utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements IRefreshTokenService{
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${refresh.expiration}")
    private Duration lifetime;

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.getExpiryTime().compareTo(Instant.now()) < 0){
            refreshRepository.delete(token);

            throw new RuntimeException(token.getToken() + " действие RefreshToken'а истекло");
        }

        return token;
    }

    @Transactional
    public RefreshToken createRefreshToken(String email){
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(email))
                .token(UUID.randomUUID().toString())
                .expiryTime(Instant.now().plus(lifetime))
                .build();

        return refreshRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken checkRefreshToken(LoginCredentials loginCredentials){
        User user = userRepository.findByEmail(loginCredentials.getEmail());

        refreshRepository.findByUserId(user.getId())
                .map(this::verifyExpiration)
                .ifPresent(refreshRepository::delete);
        refreshRepository.flush();

        return createRefreshToken(user.getEmail());
    }

    @Transactional
    public ResponseEntity<?> refreshJwtToken(RefreshRequestDto refreshRequestDto){
        return refreshRepository.findByToken(refreshRequestDto.getToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtTokenUtils.generateToken(user);
                    jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(accessToken), "Valid");

                    return ResponseEntity.ok(new TokenResponse(accessToken, refreshRequestDto.getToken()));
                })
                .orElseThrow(() -> new RuntimeException("Данного токена нет в базе данных"));
    }
}
