package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;


import lombok.Data;

import java.util.Date;

@Data
public class AllUserAndTaskDetailDTO {

    private Long taskId;
    private String taskTitle;
    private String taskDescription;
    private Long assignedToId;
    private String assignedToName;
    private Long assignedFromId;
    private String assignedFromName;
    private Long previouslyAssignedToId;
    private String previouslyAssignedToName;
    private String taskStatus;
    private Date taskCreatedOn;
    private Date assignedOn;
    private Date completedAt;

}

