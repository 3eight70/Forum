package com.hits.user.Services;

import com.hits.common.Client.ForumAppClient;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.Response.TokenResponse;
import com.hits.common.Models.Theme.ThemeDto;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Mappers.UserMapper;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
import com.hits.user.Repositories.RedisRepository;
import com.hits.user.Repositories.UserRepository;
import com.hits.user.Utils.JwtTokenUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.List;

import static com.hits.common.Consts.VERIFY_USER;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final IRefreshTokenService refreshTokenService;
    private final RedisRepository redisRepository;
    @Qualifier("com.hits.common.Client.ForumAppClient")
    private final ForumAppClient forumAppClient;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с указанным email не найден"));
    }


    @Transactional
    public ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel)
            throws MessagingException, UnsupportedEncodingException{
        User user = userRepository.findByEmail(userRegisterModel.getEmail());

        if (user != null){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с указанной почтой уже существует"), HttpStatus.BAD_REQUEST);
        }

        user = userRepository.findByLogin(userRegisterModel.getLogin());

        if (user != null){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с указанным логином уже существует"), HttpStatus.BAD_REQUEST);
        }


        user = UserMapper.userRegisterModelToUser(userRegisterModel);
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);

        userRepository.save(user);

        sendVerificationEmail(user, "http://localhost:8080");

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Письмо с подтверждением отправлено"), HttpStatus.OK);
    }

    public ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken){
        User user = userRepository.findByEmail(loginCredentials.getEmail());

        String token = jwtTokenUtils.generateToken(user);

        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");

        return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
    }

    public Boolean validateToken(String token){
        return jwtTokenUtils.validateToken(token);
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

    public UserDto getUserFromLogin(String login) {
        User user = userRepository.findByLogin(login);

        if (user == null){
            return null;
        }

        return UserMapper.userToUserDto(user);
    }

    public ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId){
        User user = userRepository.findByLogin(userDto.getLogin());
        ResponseEntity<?> checkTheme = forumAppClient.checkTheme(themeId);

        if (checkTheme.getStatusCode() == HttpStatus.OK){
            List<UUID> favoriteThemes = user.getFavoriteThemes();
            if (favoriteThemes.contains(themeId)){
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Тема с данным id уже находится в избранном пользователя"), HttpStatus.BAD_REQUEST);
            }

            favoriteThemes.add(themeId);
            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Пользователь успешно добавил тему в избранное"), HttpStatus.OK);
        }
        else if (checkTheme.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Темы с данным id не существует"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId){
        User user = userRepository.findByLogin(userDto.getLogin());

        if (user.getFavoriteThemes().contains(themeId)){
            user.getFavoriteThemes().remove(themeId);

            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Пользователь успешно удалил тему из избранного"), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Темы с данным id нет в избранном пользователя"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getFavoriteThemes(UserDto userDto){
        User user = userRepository.findByLogin(userDto.getLogin());

        return ResponseEntity.ok(forumAppClient.getThemesById(user.getFavoriteThemes()).getBody());
    }

    public ResponseEntity<?> verifyUser(UUID userId, String code){
        User user = userRepository.findUserById(userId);

        if (!user.getIsConfirmed()){
            user.setIsConfirmed(true);
            user.setVerificationCode(null);
            userRepository.saveAndFlush(user);

            String token = jwtTokenUtils.generateToken(user);
            jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
        }
        else{
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Почта пользователя уже подтверждена"), HttpStatus.BAD_REQUEST);
        }
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "gbhfns47@gmail.com";
        String senderName = "HITS.CO";
        String subject = "Пожалуйста подтвердите свою регистрацию";
        String content = "Эй, [[name]],<br>"
                + "Пожалуйста перейдите по ссылке ниже для подтверждения регистрации:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">[[URL]]</a></h3>"
                + "Спасибо,<br>"
                + "HITS COMPANY.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getLogin());
        String verifyURL = siteURL + VERIFY_USER + "?id=" + user.getId() + "&code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
}
