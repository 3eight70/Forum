package com.hits.user.Core.Auth.Service;

import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.Auth.DTO.UserRegisterModel;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

public interface AuthService {
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel) throws UserAlreadyExistsException;

    ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken) throws AccountNotConfirmedException;

    ResponseEntity<?> logoutUser(String token);
}
