package com.hits.FileSystem.Services;

import com.hits.FileSystem.Models.Dto.UserDto.LoginCredentials;
import com.hits.FileSystem.Models.Dto.UserDto.UserRegisterModel;
import com.hits.FileSystem.Models.Entity.RefreshToken;
import com.hits.FileSystem.Models.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IUserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel);
    ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken);
}
