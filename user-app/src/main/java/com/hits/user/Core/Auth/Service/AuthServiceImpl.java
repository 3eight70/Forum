package com.hits.user.Core.Auth.Service;

import com.hits.common.Core.Response.Response;
import com.hits.common.Core.Response.TokenResponse;
import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.Auth.DTO.UserRegisterModel;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import com.hits.user.Core.RefreshToken.Service.RefreshTokenService;
import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.User.Repository.RedisRepository;
import com.hits.user.Core.User.Repository.UserRepository;
import com.hits.user.Core.Utils.JwtTokenUtils;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel)
            throws MessagingException, UnsupportedEncodingException, UserAlreadyExistsException {
        String email = userRegisterModel.getEmail();
        String login = userRegisterModel.getLogin();
        String phoneNumber = userRegisterModel.getPhoneNumber();

        User user = userRepository.findByEmailOrLoginOrPhoneNumber(email, login, phoneNumber);

        if (user != null){
            if (user.getEmail().equals(email)) {
                throw new UserAlreadyExistsException(String.format("Пользователь с почтой %s уже существует",
                        email));
            }
            else if (user.getLogin().equals(login)){
                throw new UserAlreadyExistsException(String.format("Пользователь с логином %s уже существует",
                        login));
            }
            else if (user.getPhoneNumber().equals(phoneNumber)){
                throw new UserAlreadyExistsException(String.format("Пользователь с телефонным номером %s уже существует",
                        phoneNumber));
            }
        }

        user = UserMapper.userRegisterModelToUser(userRegisterModel);
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);
        user.setLogin(user.getLogin().toLowerCase());

        userRepository.saveAndFlush(user);

        String token = jwtTokenUtils.generateToken(user);
        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getLogin());

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
//        Расскоментируйте, если хотите тестить c подтверждением по почте
//        и закомментируйте кусок выше, но в таком случае, нужно в бдшке будет isConfirmed ставить true

//        sendVerificationEmail(user, "http://localhost:8080");
//
//        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Письмо с подтверждением отправлено"), HttpStatus.OK);
    }

    public ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken)
            throws AccountNotConfirmedException {
        User user = userRepository.findByLogin(loginCredentials.getLogin());

        if (!user.getIsConfirmed()){
            throw new AccountNotConfirmedException("Сперва подтвердите аккаунт");
        }

        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
    }

    public ResponseEntity<?> logoutUser(String token){
        String tokenId = "";

        if (token != null) {
            token = token.substring(7);
            tokenId = jwtTokenUtils.getIdFromToken(token);
        }
        redisRepository.delete(tokenId);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно вышел из аккаунт"), HttpStatus.OK);
    }
}
