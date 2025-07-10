package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommentAssignmentDTO {

    private String taskTitle;
    private String userName;
    private String commentContent;

}
