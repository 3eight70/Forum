package com.hits.user.Services;
<<<<<<< HEAD

import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Entity.RefreshToken;
=======

import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Entities.RefreshToken;

>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d

public interface IRefreshTokenService {
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken createRefreshToken(String email);
    RefreshToken checkRefreshToken(LoginCredentials loginCredentials);
}
