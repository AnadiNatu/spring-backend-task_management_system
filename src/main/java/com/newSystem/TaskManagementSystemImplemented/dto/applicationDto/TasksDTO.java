package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import lombok.Data;

import java.util.Date;

@Data
public class TasksDTO {

    private Long taskIds;
    private String taskTitle;
    private String taskDescription;
    private String taskComments;
    private Long assignedFromId;
    private Long assignedToId;
    private Long previouslyAssignedToId;
    private Date assignedOn;
    private Date completedAt;
    private Date taskCreatedOn;
    private String taskStatus;

}
