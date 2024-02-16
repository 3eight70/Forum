package com.hits.FileSystem.Services;

import com.hits.FileSystem.Models.Dto.UserDto.LoginCredentials;
import com.hits.FileSystem.Models.Entity.RefreshToken;
import com.hits.FileSystem.Models.Entity.User;
import com.hits.FileSystem.Repositories.RefreshRepository;
import com.hits.FileSystem.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
}
