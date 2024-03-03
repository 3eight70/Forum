package com.hits.user.Services;

import com.hits.user.Models.Dto.UserDto.LoginCredentials;
<<<<<<< HEAD
import com.hits.user.Models.Entity.RefreshToken;
import com.hits.user.Models.Entity.User;
=======
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import com.hits.user.Repositories.RefreshRepository;
import com.hits.user.Repositories.UserRepository;
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
