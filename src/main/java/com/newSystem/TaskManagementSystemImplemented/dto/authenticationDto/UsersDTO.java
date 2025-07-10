package com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import lombok.Data;

import java.util.List;

@Data
public class UsersDTO {

    private Long id;
    private String name;
    private String username;
    private String password;
    private int age;
    private String department;
    private boolean taskAssignment;
    private int completeTask;
    private UserRoles userRoles;
    private List<Long> taskIds;
}
