package com.hits.user.Core.RefreshToken.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель запроса токена")
public class RefreshRequestDto {
    @Schema(description = "access токен")
    private String token;
}
