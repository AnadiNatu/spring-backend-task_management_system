package com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import lombok.Data;

@Data
public class LoginResponse {

    private Long id;
    private String jwt;
    private UserRoles userRoles;

}
