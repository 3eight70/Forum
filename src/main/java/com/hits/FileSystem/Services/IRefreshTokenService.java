package com.hits.FileSystem.Services;

import com.hits.FileSystem.Models.Dto.UserDto.LoginCredentials;
import com.hits.FileSystem.Models.Entity.RefreshToken;

public interface IRefreshTokenService {
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
    RefreshToken checkRefreshToken(LoginCredentials loginCredentials);
}
