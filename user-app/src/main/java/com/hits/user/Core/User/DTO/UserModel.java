package com.hits.user.Core.User.DTO;

import com.hits.common.Core.User.DTO.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель пользователя")
public class UserModel {
    @NotNull
    @Schema(description = "Идентификатор пользователя")
    private UUID id;

    @NotNull
    @Schema(description = "Время создания пользователя")
    private LocalDateTime createTime;

    @NotNull
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Неверный адрес электронной почты")
    @Schema(description = "Адрес электронной почты", example = "example@example.ru")
    private String email;

    @NotNull
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+|([a-zA-Z0-9]+)", message = "Логин должен состоять из букв и цифр")
    @Schema(description = "Логин пользователя", example = "example")
    private String login;

    @NotNull
    @Schema(description = "Статус подтверждения аккаунта пользователя", example = "true")
    private Boolean isConfirmed;

    @NotNull
    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;

    @Schema(description = "Идентификатор категорий, управляемых пользователем, являющимся модератором")
    private List<UUID> manageCategoryId;
}
