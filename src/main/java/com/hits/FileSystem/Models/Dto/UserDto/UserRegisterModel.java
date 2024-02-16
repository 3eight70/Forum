package com.hits.FileSystem.Models.Dto.UserDto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor

public class UserRegisterModel {
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Неверный адрес электронной почты")
    private String email;

    @Pattern(regexp = "^(?=.*\\d).{6,}$", message = "Пароль должен содержать не менее 6 символов и 1 цифры")
    private String password;
}
