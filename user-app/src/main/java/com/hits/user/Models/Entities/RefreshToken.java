<<<<<<<< HEAD:user-app/src/main/java/com/hits/user/Models/Entity/RefreshToken.java
package com.hits.user.Models.Entity;
========
package com.hits.user.Models.Entities;
>>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d:user-app/src/main/java/com/hits/user/Models/Entities/RefreshToken.java

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Instant expiryTime;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
