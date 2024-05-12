package com.hits.user.Core.Auth.Service;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.Notification.Proto.NotificationDTOOuterClass;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.Response.TokenResponse;
import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.Auth.DTO.UserRegisterModel;
import com.hits.user.Core.Kafka.KafkaProducer;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import com.hits.user.Core.RefreshToken.Repository.RefreshRepository;
import com.hits.user.Core.RefreshToken.Service.RefreshTokenService;
import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.User.Repository.RedisRepository;
import com.hits.user.Core.User.Repository.UserRepository;
import com.hits.user.Core.Utils.JwtTokenUtils;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hits.common.Core.Consts.VERIFY_USER;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshRepository refreshRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel)
            throws UserAlreadyExistsException {
        String login = userRegisterModel.getLogin();
        userRegisterModel.setLogin(login == null ? userRegisterModel.getEmail() : login.toLowerCase());

        String email = userRegisterModel.getEmail();
        login = userRegisterModel.getLogin();
        String phoneNumber = userRegisterModel.getPhoneNumber();

        Optional<User> userOptional = userRepository.findByEmailOrLoginOrPhoneNumber(email, login, phoneNumber);
        User user;

        if (userOptional != null && userOptional.isPresent()){
            user = userOptional.get();

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

        String content = "Эй, [[name]],<br>"
                + "Пожалуйста перейдите по ссылке ниже для подтверждения регистрации:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ПОДТВЕРДИ МЕНЯ</a></h3>"
                + "Спасибо,<br>"
                + "HITS COMPANY.";

        content = content.replace("[[name]]", user.getLogin());
        String verifyURL = "http://localhost:8080" + VERIFY_USER + "?id=" + user.getId() + "&code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        List<NotificationDTOOuterClass.NotificationChannel> channels = new ArrayList<>();
        channels.add(NotificationDTOOuterClass.NotificationChannel.EMAIL);

        kafkaProducer.sendMessage(UserMapper.userToUserDto(user),
                "Пожалуйста подтвердите свою регистрацию",
                content,
                channels,
                false);

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
    }

    @Transactional
    public ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken)
            throws AccountNotConfirmedException {
        User user = userRepository.findByLogin(loginCredentials.getLogin()).get();

        if (!user.getIsConfirmed()){
            throw new AccountNotConfirmedException("Сперва подтвердите аккаунт");
        }

        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
    }

    @Transactional
    public ResponseEntity<?> logoutUser(String token){
        String tokenId = "";
        String userId = "";

        if (token != null) {
            token = token.substring(7);
            tokenId = jwtTokenUtils.getIdFromToken(token);
            userId = jwtTokenUtils.getUserId(token);
        }
        Optional<RefreshToken> refreshToken = refreshRepository.findByUserId(UUID.fromString(userId));

        refreshToken.ifPresent(value -> refreshRepository.deleteRefreshTokenById(value.getId()));

        redisRepository.delete(tokenId);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно вышел из аккаунт"), HttpStatus.OK);
    }
}
