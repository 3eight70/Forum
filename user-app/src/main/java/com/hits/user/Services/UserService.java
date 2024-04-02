package com.hits.user.Services;

import com.hits.security.Client.ForumAppClient;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.UnknownException;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.Response.TokenResponse;
import com.hits.common.Models.User.Role;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import com.hits.user.Mappers.UserMapper;
import com.hits.user.Models.Dto.UserDto.CreateUserModel;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserEditModel;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
import com.hits.user.Repositories.RedisRepository;
import com.hits.user.Repositories.UserRepository;
import com.hits.user.Utils.JwtTokenUtils;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import static com.hits.common.Consts.VERIFY_USER;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final IRefreshTokenService refreshTokenService;
    private final RedisRepository redisRepository;
    @Qualifier("com.hits.security.Client.ForumAppClient")
    private final ForumAppClient forumAppClient;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public User loadUserByUsername(String login) throws UsernameNotFoundException{
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с указанным логином не найден"));
    }


    @Transactional
    public ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel)
            throws MessagingException, UnsupportedEncodingException, UserAlreadyExistsException{
        String email = userRegisterModel.getEmail();
        String login = userRegisterModel.getLogin();
        String phoneNumber = userRegisterModel.getPhoneNumber();

        User user = userRepository.findByEmailOrLoginOrPhoneNumber(email, login, phoneNumber);

        checkUser(user, email, login, phoneNumber);

        user = UserMapper.userRegisterModelToUser(userRegisterModel);
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);
        user.setLogin(user.getLogin().toLowerCase());

        userRepository.save(user);

        String token = jwtTokenUtils.generateToken(user);
        jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

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

    public Boolean validateToken(String token){
        try {
            return jwtTokenUtils.validateToken(token);
        }
        catch (ExpiredJwtException | FeignException.Unauthorized e){
            return false;
        }
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

    public UserDto getUserFromLogin(String login) throws NotFoundException{
        User user = userRepository.findByLogin(login);

        if (user == null){
            throw new NotFoundException(String.format("Пользователя с логином=%s не существует", login));
        }

        return UserMapper.userToUserDto(user);
    }

    public ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId) throws NotFoundException{
        User user = userRepository.findByLogin(userDto.getLogin());
        ResponseEntity<?> checkTheme;
        try {
             checkTheme = forumAppClient.checkTheme(themeId);
        }
        catch (FeignException.NotFound e){
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }

        if (checkTheme != null && checkTheme.getStatusCode() == HttpStatus.OK){
            List<UUID> favoriteThemes = user.getFavoriteThemes();
            if (favoriteThemes.contains(themeId)){
                throw new BadRequestException(String.format("Тема с id=%s уже находится в избранном пользователя", themeId));
            }

            favoriteThemes.add(themeId);
            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Пользователь успешно добавил тему в избранное"), HttpStatus.OK);
        }

        throw new UnknownException();
    }

    public ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId) throws NotFoundException{
        User user = userRepository.findByLogin(userDto.getLogin());

        if (user.getFavoriteThemes().contains(themeId)){
            user.getFavoriteThemes().remove(themeId);

            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Пользователь успешно удалил тему из избранного"), HttpStatus.OK);
        }
        else{
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }
    }

    public ResponseEntity<?> getFavoriteThemes(UserDto userDto){
        User user = userRepository.findByLogin(userDto.getLogin());

        return ResponseEntity.ok(forumAppClient.getThemesById(user.getFavoriteThemes()).getBody());
    }

    public ResponseEntity<?> verifyUser(UUID userId, String code) throws NotFoundException, BadRequestException{
        User user = userRepository.findUserById(userId);

        if (user == null){
            throw new NotFoundException(String.format("Пользователя с id=%s не существует", userId));
        }

        if (!user.getIsConfirmed()){
            if (code.equals(user.getVerificationCode())) {
                user.setIsConfirmed(true);
                user.setVerificationCode(null);
                userRepository.saveAndFlush(user);

                String token = jwtTokenUtils.generateToken(user);
                jwtTokenUtils.saveToken(jwtTokenUtils.getIdFromToken(token), "Valid");
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                return ResponseEntity.ok(new TokenResponse(token, refreshToken.getToken()));
            }
            throw new BadRequestException("Неверный код подтверждения аккаунта");
        }
        else{
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Почта пользователя уже подтверждена"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> banUser(UserDto user, UUID userId) throws NotFoundException, BadRequestException{
        User userToBan = userRepository.findUserById(userId);

        if (userToBan == null){
            throw new NotFoundException(String.format("Пользователя с id=%s не существует", userId));
        }

        if (userToBan.getIsBanned()){
            throw new BadRequestException(String.format("Пользователь с id=%s уже заблокирован", userId));
        }

        userToBan.setIsBanned(true);
        userRepository.saveAndFlush(userToBan);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно заблокирован"), HttpStatus.OK);
    }

    public ResponseEntity<?> giveModeratorRole(UserDto userDto, UUID userId) throws NotFoundException{
        User user = userRepository.findUserById(userId);

        if (user == null){
            throw new NotFoundException(String.format("Пользователя с id=%s не существует", userId));
        }

        if (user.getRole() == Role.MODERATOR){
            throw new BadRequestException("Пользователь уже имеет роль модератора");
        }

        user.setRole(Role.MODERATOR);
        userRepository.saveAndFlush(user);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Роль модератора успешно выдана"), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteModeratorRole(UserDto userDto, UUID userId) throws NotFoundException {
        User user = userRepository.findUserById(userId);

        if (user == null){
            throw new NotFoundException(String.format("Пользователя с id=%s не существует", userId));
        }

        if (user.getRole() != Role.MODERATOR){
            throw new BadRequestException("Пользователь не имеет роли модератора");
        }

        user.setRole(Role.USER);
        user.setManageCategoryId(null);
        userRepository.saveAndFlush(user);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Роль модератора успешно удалена"), HttpStatus.OK);
    }

    public ResponseEntity<?> giveCategoryToModerator(UserDto userDto, UUID userId, UUID categoryId)
            throws NotFoundException, BadRequestException {
        User user = userRepository.findUserById(userId);

        if (user == null){
            throw new NotFoundException(String.format("Пользователя с id=%s не существует", userId));
        }

        if (user.getRole() == Role.ADMIN){
            throw new BadRequestException("Пользователь является администратором");
        }
        else if (user.getRole() != Role.MODERATOR){
            throw new BadRequestException(String.format("Пользователь с id=%s не является модератором", userId));
        }

        ResponseEntity<?> checkCategory;
        try {
            checkCategory = forumAppClient.checkCategory(categoryId);
        }
        catch (FeignException.NotFound e){
            throw new NotFoundException(String.format("Категории с id=%s не существует", categoryId));
        }

        if (checkCategory != null && checkCategory.getStatusCode() == HttpStatus.OK){
            user.setManageCategoryId(categoryId);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Модератор успешно назначен на категорию"), HttpStatus.OK);
        }

        throw new UnknownException();
    }

    public ResponseEntity<?> createUser(CreateUserModel createUserModel) {
        String email = createUserModel.getEmail();
        String login = createUserModel.getLogin();
        String phoneNumber = createUserModel.getPhoneNumber();

        User user = userRepository.findByEmailOrLoginOrPhoneNumber(email, login, phoneNumber);

        checkUser(user, email, login, phoneNumber);

        user = UserMapper.createUserModelToUser(createUserModel);
        user.setLogin(user.getLogin().toLowerCase());

        userRepository.save(user);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно создан"), HttpStatus.OK);
    }

    public ResponseEntity<?> editUser(UserEditModel userEditModel, UUID userId){
        User user = userRepository.findUserById(userId);

        if (user == null){
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }

        String email = userEditModel.getEmail();
        String phoneNumber = userEditModel.getPhoneNumber();

        User userCheckEmail = userRepository.findByEmail(email);
        User userCheckPhone = userRepository.findByPhoneNumber(phoneNumber);

        if (userCheckEmail != user) {
            checkUser(userCheckEmail, email, null, phoneNumber);
        }

        if (userCheckPhone != user) {
            checkUser(userCheckPhone, email, null, phoneNumber);
        }

        user.setEmail(email);
        user.setPassword(userEditModel.getPassword());
        user.setRole(userEditModel.getRole());
        user.setPhoneNumber(phoneNumber);

        userRepository.save(user);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно отредактирован"), HttpStatus.OK);
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "gbhfns47@gmail.com";
        String senderName = "HITS.CO";
        String subject = "Пожалуйста подтвердите свою регистрацию";
        String content = "Эй, [[name]],<br>"
                + "Пожалуйста перейдите по ссылке ниже для подтверждения регистрации:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ПОДТВЕРДИ МЕНЯ</a></h3>"
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

    private void checkUser (User user, String email, String login, String phoneNumber){
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
    }
}
