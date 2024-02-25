package com.hits.user.Services;

import com.hits.user.Mappers.UserMapper;
import com.hits.user.Models.Dto.Response.Response;
import com.hits.user.Models.Dto.Response.TokenResponse;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entity.RefreshToken;
import com.hits.user.Models.Entity.User;
import com.hits.user.Repositories.UserRepository;
import com.hits.user.Utils.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final IRefreshTokenService refreshTokenService;

    @Transactional
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с указанным email не найден"));
    }


    @Transactional
    public ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel){
        User user = userRepository.findByEmail(userRegisterModel.getEmail());

        if (user != null){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с указанной почтой уже существует"), HttpStatus.BAD_REQUEST);
        }

        user = UserMapper.userRegisterModelToUser(userRegisterModel);
        userRepository.save(user);

        String token = jwtTokenUtils.generateToken(user);
        jwtTokenUtils.saveToken(token, "Valid");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
    }

    public ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken){
        User user = userRepository.findByEmail(loginCredentials.getEmail());

        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
    }
}
