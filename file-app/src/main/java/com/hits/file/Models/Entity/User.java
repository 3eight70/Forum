package com.hits.file.Models.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(unique = true, nullable = false)
    @Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @Pattern(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+", message = "Неверный адрес электронной почты")
    private String email;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    @Pattern(regexp = "^(?=.*\\d).{6,}$", message = "Пароль должен содержать не менее 6 символов и 1 цифры")
    private String password;

    @PrePersist
    private void init(){
        createTime = LocalDateTime.now();
    }
}