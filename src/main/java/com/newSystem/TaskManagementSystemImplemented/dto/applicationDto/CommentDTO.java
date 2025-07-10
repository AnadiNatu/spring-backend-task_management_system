package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.CommentByRole;
import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String commentContent;
    private CommentByRole commentByRole;
    private String taskTitle;
    private TaskStatus taskStatus;
    private String commentByUserName;
    private UserRoles userRoles;
}
