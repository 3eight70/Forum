package com.hits.user.Core.RefreshToken.Entity;

import com.hits.user.Core.User.Entity.User;
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
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiryTime;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;
}
