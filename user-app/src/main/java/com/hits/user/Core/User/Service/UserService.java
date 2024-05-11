package com.hits.user.Core.User.Service;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.user.Core.User.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;

    UserDto getUserFromLogin(String login) throws NotFoundException;

    ResponseEntity<?> verifyUser(UUID userId, String code) throws NotFoundException, BadRequestException;

    Boolean validateToken(String token);
}