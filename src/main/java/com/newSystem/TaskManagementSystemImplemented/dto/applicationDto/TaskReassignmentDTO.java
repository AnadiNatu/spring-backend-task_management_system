package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskReassignmentDTO {

    private Long taskId;
    private String taskTitle;
    private String taskComment;
    private String assignedFrom;
    private String assignedTo;
    private String previouslyAssignedTo;
    private Date assignedOn;
    private Date previouslyAssignedOn;

}
