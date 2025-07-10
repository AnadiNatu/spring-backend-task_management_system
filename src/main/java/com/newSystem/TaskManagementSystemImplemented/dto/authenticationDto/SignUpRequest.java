package com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import lombok.Data;

@Data
public class SignUpRequest {

    private String name;
    private String username;
    private String password;
    private int age;
    private String department;
    private UserRoles userRoles;

}
