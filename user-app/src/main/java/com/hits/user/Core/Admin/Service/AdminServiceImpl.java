package com.hits.user.Core.Admin.Service;

import com.hits.common.Core.Category.DTO.CategoryDto;
import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.UnknownException;
import com.hits.security.Rest.Client.ForumAppClient;
import com.hits.user.Core.Admin.DTO.CreateUserModel;
import com.hits.user.Core.Admin.DTO.UserEditModel;
import com.hits.user.Core.Kafka.KafkaProducer;
import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.User.Repository.UserRepository;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ForumAppClient forumAppClient;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public ResponseEntity<?> banUser(UserDto user, UUID userId) throws NotFoundException, BadRequestException {
        User userToBan = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%s не существует", userId)));

        if (userToBan.getIsBanned()){
            throw new BadRequestException(String.format("Пользователь с id=%s уже заблокирован", userId));
        }

        userToBan.setIsBanned(true);
        userRepository.saveAndFlush(userToBan);

        List<NotificationChannel> channels = new ArrayList<>();
        channels.add(NotificationChannel.EMAIL);

        kafkaProducer.sendMessage(
                UserMapper.userToUserDto(userToBan),
                "Блокировка",
                "Вы были заблокированы",
                channels,
                true
        );

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно заблокирован"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> unbanUser(UserDto user, UUID userId) throws NotFoundException, BadRequestException {
        User userToUnBan = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%s не существует", userId)));

        if (!userToUnBan.getIsBanned()){
            throw new BadRequestException(String.format("Пользователь с id=%s не находится в блокировке", userId));
        }

        userToUnBan.setIsBanned(false);
        userRepository.saveAndFlush(userToUnBan);

        List<NotificationChannel> channels = new ArrayList<>();
        channels.add(NotificationChannel.EMAIL);

        kafkaProducer.sendMessage(
                UserMapper.userToUserDto(userToUnBan),
                "Разблокировка",
                "Вы были разблокированы",
                channels,
                true
        );

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно разблокирован"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> giveModeratorRole(UserDto userDto, UUID userId) throws NotFoundException{
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%s не существует", userId)));

        if (user.getRole() == Role.MODERATOR){
            throw new BadRequestException("Пользователь уже имеет роль модератора");
        }

        user.setRole(Role.MODERATOR);
        userRepository.saveAndFlush(user);

        List<NotificationChannel> channels = new ArrayList<>();
        channels.add(NotificationChannel.EMAIL);

        kafkaProducer.sendMessage(
                UserMapper.userToUserDto(user),
                "Смена роли",
                "Вам была выдана роль модератора",
                channels,
                true
        );

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Роль модератора успешно выдана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteModeratorRole(UserDto userDto, UUID userId) throws NotFoundException {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%s не существует", userId)));

        if (user.getRole() != Role.MODERATOR){
            throw new BadRequestException("Пользователь не имеет роли модератора");
        }

        user.setRole(Role.USER);
        user.setManageCategoryId(null);
        userRepository.saveAndFlush(user);

        List<NotificationChannel> channels = new ArrayList<>();
        channels.add(NotificationChannel.EMAIL);

        kafkaProducer.sendMessage(
                UserMapper.userToUserDto(user),
                "Смена роли",
                "Вас сняли с роли модератора",
                channels,
                true
        );

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Роль модератора успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> giveCategoryToModerator(UserDto userDto, UUID userId, UUID categoryId)
            throws NotFoundException, BadRequestException {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%s не существует", userId)));

        if (user.getRole() == Role.ADMIN){
            throw new BadRequestException("Пользователь является администратором");
        }
        else if (user.getRole() != Role.MODERATOR){
            throw new BadRequestException(String.format("Пользователь с id=%s не является модератором", userId));
        }

        ResponseEntity<CategoryDto> checkCategory;
        try {
            checkCategory = forumAppClient.checkCategory(categoryId);
        }
        catch (FeignException.NotFound e){
            throw new NotFoundException(String.format("Категории с id=%s не существует", categoryId));
        }

        if (checkCategory != null && checkCategory.getStatusCode() == HttpStatus.OK){
            List<UUID> manageCategoriesIds = new ArrayList<>();
            manageCategoriesIds.addAll(Objects.requireNonNull(checkCategory.getBody()).getChildCategories()
                    .stream()
                    .map(CategoryDto::getId)
                    .toList());
            manageCategoriesIds.add(categoryId);
            user.setManageCategoryId(manageCategoriesIds);

            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(NotificationChannel.EMAIL);

            kafkaProducer.sendMessage(
                    UserMapper.userToUserDto(user),
                    "Назначение на категорию",
                    String.format("Вас назначили на управлению категорией с id=%s", categoryId),
                    channels,
                    true
            );

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Модератор успешно назначен на категорию"), HttpStatus.OK);
        }

        throw new UnknownException();
    }

    @Transactional
    public ResponseEntity<?> createUser(CreateUserModel createUserModel) {
        String email = createUserModel.getEmail();
        String login = createUserModel.getLogin();
        String phoneNumber = createUserModel.getPhoneNumber();

        User user = userRepository.findByEmailOrLoginOrPhoneNumber(email, login, phoneNumber).get();

        checkUser(user, email, login, phoneNumber);

        user = UserMapper.createUserModelToUser(createUserModel);
        user.setLogin(user.getLogin().toLowerCase());

        userRepository.save(user);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Пользователь успешно создан"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editUser(UserEditModel userEditModel, UUID userId){
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));

        String email = userEditModel.getEmail();
        String phoneNumber = userEditModel.getPhoneNumber();

        User userCheckEmail = userRepository.findByEmail(email).get();
        User userCheckPhone = userRepository.findByPhoneNumber(phoneNumber).get();

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
