package com.hits.common.Core.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationDto {
    private UUID userId;
    private String email;
    private String verificationCode;
    private String login;
}
