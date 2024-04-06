package com.hits.user.Core.RefreshToken.Entity;

import com.hits.user.Core.User.Entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
@Builder
@Schema(description = "Сущность refresh токен")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Идентификатор токена")
    private UUID id;

    @Column(nullable = false)
    @Schema(description = "access токен")
    private String token;

    @Column(nullable = false)
    @Schema(description = "Время просрочки токена")
    private Instant expiryTime;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;
}
