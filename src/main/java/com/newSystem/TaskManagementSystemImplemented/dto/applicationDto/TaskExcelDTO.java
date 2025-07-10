package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskExcelDTO {

    private Long taskId;
    private String taskTitle;
    private String department;
    private Long assignedFromUserId;
    private String assignedFromUserName;
    private Long assignedToUserId;
    private String assignedToUserName;
    private Long previouslyAssignedToUserId;
    private String previouslyAssignedToUserName;
    private TaskStatus taskStatus;
}
