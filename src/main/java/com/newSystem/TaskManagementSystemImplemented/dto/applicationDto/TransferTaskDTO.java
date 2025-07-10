package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class TransferTaskDTO {

    private Long taskId;
    private String taskTitle;
    private String assignedFrom;
    private String assignedTo;
    private String previouslyAssignedTo;
    private Date assignedOn;
    private TaskStatus taskStatus;

}
