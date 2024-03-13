package com.hits.user.Services;

import com.hits.common.Models.User.UserDto;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel);
    ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken);
    Boolean validateToken(String token);
    ResponseEntity<?> logoutUser(String token);
    UserDto getUserFromLogin(String login);
}
