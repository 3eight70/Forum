package com.hits.user.Core.RefreshToken.Service;

import com.hits.common.Core.Response.TokenResponse;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.RefreshToken.DTO.RefreshRequestDto;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import com.hits.user.Core.RefreshToken.Repository.RefreshRepository;
import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Repository.UserRepository;
import com.hits.user.Core.Utils.JwtTokenUtils;
import com.hits.user.Exceptions.ExpiredTokenException;
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
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${refresh.expiration}")
    private Duration lifetime;

    @Transactional()
    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.getExpiryTime().compareTo(Instant.now()) < 0){
            refreshRepository.deleteRefreshTokenById(token.getId());

            return null;
        }

        return token;
    }

    @Transactional
    public RefreshToken createRefreshToken(String login){
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByLogin(login).get())
                .token(UUID.randomUUID().toString())
                .expiryTime(Instant.now().plus(lifetime))
                .build();

        return refreshRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken checkRefreshToken(LoginCredentials loginCredentials){
        User user = userRepository.findByLogin(loginCredentials.getLogin())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        refreshRepository.findByUserId(user.getId())
                .map(this::verifyExpiration)
                .ifPresent(refreshRepository::delete);
        refreshRepository.flush();

        return createRefreshToken(user.getLogin());
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
                .orElseThrow(() -> new ExpiredTokenException("Срок действия токена истек"));
    }
}
