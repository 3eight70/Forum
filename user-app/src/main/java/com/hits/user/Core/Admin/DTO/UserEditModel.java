package com.hits.user.Core.Admin.DTO;

import com.hits.common.Models.User.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель редактирования пользователя")
public class UserEditModel {
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Неверный адрес электронной почты")
    @NotNull(message = "Адрес почты должен быть указан")
    @Schema(description = "Адрес электронной почты", example = "example@example.ru")
    private String email;

    @Pattern(regexp = "^\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}$", message = "Телефон должен быть указан в формате +7 (xxx) xxx-xx-xx")
    @NotNull(message = "Номер телефона должен быть указан")
    @Schema(description = "Телефонный номер", example = "+7 (777) 777-77-77")
    private String phoneNumber;

    @Pattern(regexp = "^(?=.*\\d).{6,}$", message = "Пароль должен содержать не менее 6 символов и 1 цифры")
    @NotNull(message = "Пароль должен быть указан")
    @Schema(description = "Пароль пользователя", example = "qwerty12345")
    private String password;

    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;

    @Schema(description = "Статус подтверждения аккаунта пользователя", example = "true")
    private Boolean isConfirmed;
}
