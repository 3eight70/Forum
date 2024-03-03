package com.hits.user.Services;

import com.hits.user.Models.Dto.Token.RefreshRequestDto;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Entities.RefreshToken;
import org.springframework.http.ResponseEntity;


public interface IRefreshTokenService {
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
    RefreshToken checkRefreshToken(LoginCredentials loginCredentials);
    ResponseEntity<?> refreshJwtToken(RefreshRequestDto refreshRequestDto);
}
