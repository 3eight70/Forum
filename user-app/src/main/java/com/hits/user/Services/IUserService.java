package com.hits.user.Services;

import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
<<<<<<< HEAD
import com.hits.user.Models.Entity.RefreshToken;
import com.hits.user.Models.Entity.User;
=======

import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IUserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel);
    ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken);
    Boolean validateToken(String token);
    ResponseEntity<?> logoutUser(String token);
}
