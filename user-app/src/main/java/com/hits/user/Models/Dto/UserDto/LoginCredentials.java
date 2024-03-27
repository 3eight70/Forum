package com.hits.user.Models.Dto.UserDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCredentials {
    @NotNull(message = "Адрес почты должен быть указан")
    @Size(min = 1, message = "Минимальная длина адреса почты равна 1")
    private String email;

    @NotNull(message = "Пароль должен быть указан")
    @Size(min = 1, message = "Минимальная длина пароля равна 1")
    private String password;
}
