package com.hits.user.Services;

import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.common.Entities.RefreshToken;

public interface IRefreshTokenService {
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
    RefreshToken checkRefreshToken(LoginCredentials loginCredentials);
}
