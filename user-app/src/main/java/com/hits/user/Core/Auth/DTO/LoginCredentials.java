package com.hits.user.Core.Auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCredentials {
    @NotNull(message = "Логин должен быть указан")
    @Size(min = 1, message = "Минимальная длина логина равна 1")
    @Schema(description = "Логин пользователя", example = "example")
    private String login;

    @NotNull(message = "Пароль должен быть указан")
    @Size(min = 1, message = "Минимальная длина пароля равна 1")
    @Schema(description = "Пароль пользователя", example = "qwerty12345")
    private String password;
}
