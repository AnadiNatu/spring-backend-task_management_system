package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import lombok.Data;


@Data
public class TaskStatusUpdateDTO {

    private Long taskId;
    private String taskTitle;
    private String taskStatus;

}
