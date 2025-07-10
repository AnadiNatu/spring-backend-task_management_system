package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class TaskFilterDTO {
    private String taskTitle;
    private TaskStatus taskStatus;
    private Long assignedToId;
    private Long assignedFromId;
    private Date assignedOn;
    private Date completedAt;
}
