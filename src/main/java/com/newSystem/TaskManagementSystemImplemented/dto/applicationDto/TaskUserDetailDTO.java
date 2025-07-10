package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskUserDetailDTO {

    private Long userCompletedId;
    private Long taskId;
    private String taskTitle;
    private Date completedAt;
    private String taskDescription;
    private String taskStatus;
    private String assignedFrom;
    private String completedBy;

}
