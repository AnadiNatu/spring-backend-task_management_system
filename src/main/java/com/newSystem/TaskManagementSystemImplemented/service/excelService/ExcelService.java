package com.newSystem.TaskManagementSystemImplemented.service.excelService;

import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.TaskExcelDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.entity.TaskComment;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.mapper.TaskUserNewMapper;
import com.newSystem.TaskManagementSystemImplemented.repository.CommentRepository;
import com.newSystem.TaskManagementSystemImplemented.repository.TaskRepository;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ExcelService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TaskUserNewMapper taskMapper;

    public List<Task> getAllTasks(){

        return taskRepository.findAll();

    }

    public List<Users> getAllUsers(){

        return userRepository.findAll();

    }

    public List<TaskComment> getAllComments(){

        return commentRepository.findAll();

    }

    public List<TaskExcelDTO> getTaskExcelDTO(){

        List<Task> taskList = taskRepository.findAll();

        return taskMapper.toTaskExcelDTO(taskList);
    }
}
