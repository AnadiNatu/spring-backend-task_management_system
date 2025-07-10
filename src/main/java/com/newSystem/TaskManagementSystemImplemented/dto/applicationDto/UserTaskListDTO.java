package com.newSystem.TaskManagementSystemImplemented.dto.applicationDto;

import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import lombok.Data;

import java.util.List;

@Data
public class UserTaskListDTO {

    String name;
    List<Task> listOfTask;

}
