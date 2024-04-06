package com.hits.user.Core.RefreshToken.Service;

import com.hits.user.Core.RefreshToken.DTO.RefreshRequestDto;
import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import org.springframework.http.ResponseEntity;


public interface RefreshTokenService {
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
    RefreshToken checkRefreshToken(LoginCredentials loginCredentials);
    ResponseEntity<?> refreshJwtToken(RefreshRequestDto refreshRequestDto);
}
