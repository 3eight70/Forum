package com.hits.user.Core.Admin.Service;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.UnknownException;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.User.Role;
import com.hits.common.Models.User.UserDto;
import com.hits.security.Rest.Client.ForumAppClient;
import com.hits.user.Core.Admin.DTO.CreateUserModel;
import com.hits.user.Core.Admin.DTO.UserEditModel;
import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.User.Repository.UserRepository;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ForumAppClient forumAppClient;

    public ResponseEntity<?> banUser(UserDto user, UUID userId) throws NotFoundException, BadRequestException {
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
        user.setIsConfirmed(userEditModel.getIsConfirmed());

        userRepository.save(user);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно отредактирован"), HttpStatus.OK);
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
