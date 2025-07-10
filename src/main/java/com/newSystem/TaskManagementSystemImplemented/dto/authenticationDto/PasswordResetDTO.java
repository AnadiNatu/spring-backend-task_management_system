package com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto;

import lombok.Data;

@Data
public class PasswordResetDTO {

    private String username;
    private String resetToken;
    private String newPassword;

}
