package com.hits.common.Core.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с токеном")
public class TokenResponse {
    @Schema(description = "jwt токен")
    private String accessToken;

    @Schema(description = "refresh токен")
    private String token;
}
