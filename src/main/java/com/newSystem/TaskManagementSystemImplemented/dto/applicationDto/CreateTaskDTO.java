package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import lombok.Data;

@Data
public class CreateTaskDTO {
    private String taskTitle;
    private String taskDescription;
    private String assignedFrom;
    private TaskStatus taskStatus;
}
