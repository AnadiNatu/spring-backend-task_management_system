package com.newSystem.TaskManagementSystemImplemented.service.commentService;


import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.CommentAssignmentDTO;
import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.CommentDTO;
import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.CreateTaskDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.entity.TaskComment;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.enums.CommentByRole;
import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import com.newSystem.TaskManagementSystemImplemented.exception.ResourceNotFoundException;
import com.newSystem.TaskManagementSystemImplemented.mapper.TaskUserNewMapper;
import com.newSystem.TaskManagementSystemImplemented.repository.CommentRepository;
import com.newSystem.TaskManagementSystemImplemented.repository.TaskRepository;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TaskUserNewMapper taskMapper;

    public Task createTaskWithOptionalComment(CreateTaskDTO createTaskDTO , String commentContent){

        Task task = new Task();

        task.setTaskTitle(createTaskDTO.getTaskTitle());
        task.setTaskDescription(createTaskDTO.getTaskDescription());
        task.setAssignedFrom(userRepository.findUserByNameContaining(createTaskDTO.getAssignedFrom()).orElseThrow(() -> new RuntimeException("Assigned from user not found")));

        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskCreatedOn(new Date());

        Task savedTask = taskRepository.save(task);

        if (commentContent != null && !commentContent.isEmpty()){
            TaskComment comments = new TaskComment();
            comments.setCommentContent(commentContent);
            comments.setCommentByRole(CommentByRole.CREATOR);
            comments.setCommentedByUser(task.getAssignedFrom());
            comments.setTasks(savedTask);
            commentRepository.save(comments);
        }

        return savedTask;

    }


    public CommentDTO addEmployeeCommentOnAssignedTask(CommentAssignmentDTO commentDTO) {
        return taskMapper.toCommentDto(addEmployeeComment(commentDTO, CommentByRole.ASSIGNED_TO));
    }


    public CommentDTO addEmployeeCommentOnReAssignedTask(CommentAssignmentDTO commentDTO) {
        return taskMapper.toCommentDto(addEmployeeComment(commentDTO, CommentByRole.REASSIGNED_TO));
    }


    public List<CommentDTO> getAllCommentsByTaskTitle(String taskTitle) {
        ensureTaskExists(taskTitle);

        List<TaskComment> commentList = commentRepository.findAllCommentByTaskTitle(taskTitle);

        List<CommentDTO> dtoList = new ArrayList<>();

        for (TaskComment comment : commentList){
            dtoList.add(taskMapper.toCommentDto(comment));
        }

        return dtoList;
    }


    public List<CommentDTO> getCommentsOnTaskByEmployee(String taskTitle, String employeeName) {
        List<TaskComment> commentList = filterCommentsByEmployee(taskTitle, employeeName, CommentByRole.ASSIGNED_TO);

        List<CommentDTO> dtoList = new ArrayList<>();

        for (TaskComment comment : commentList){
            dtoList.add(taskMapper.toCommentDto(comment));
        }

        return dtoList;
    }


    public List<CommentDTO> getCommentsOnTaskByReassignedEmployee(String taskTitle, String employeeName) {


        List<TaskComment> commentList = filterCommentsByEmployee(taskTitle, employeeName, CommentByRole.REASSIGNED_TO);

        List<CommentDTO> dtoList = new ArrayList<>();

        for (TaskComment comment : commentList){
            dtoList.add(taskMapper.toCommentDto(comment));
        }

        return dtoList;
    }


    public CommentDTO getCommentById(Long commentId){

        TaskComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID : " + commentId));

        return taskMapper.toCommentDto(comment);
    }


    public List<CommentDTO> getCommentByUserName(String userName){

        Users users = userRepository
                .findUserByNameContaining(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: "+ userName));

        List<TaskComment> comments = commentRepository
                .findAll()
                .stream()
                .filter(c -> c.getCommentedByUser() != null &&
                             c.getCommentedByUser().getName().equalsIgnoreCase(userName))
                .collect(Collectors.toList());

        return comments.stream().map(t -> taskMapper.toCommentDto(t)).collect(Collectors.toList());
    }


    public List<CommentDTO> getCommentByEmployeeBetweenDates(String taskTitle , String employeeName , Date to , Date from){

        Users employee = userRepository.findUserByNameContaining(employeeName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " +employeeName));

        Task task = taskRepository.findByTaskTitle(taskTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskTitle));

        return
                commentRepository
                        .findAll()
                        .stream()
                        .filter(c -> c.getTasks().getTaskIds().equals(task.getTaskIds())
                        && c.getCommentedByUser().getId().equals(employee.getId())
                        && c.getCreatedAt() != null
                        && !c.getCreatedAt().before(from)
                        && !c.getCreatedAt().after(to))
                        .map(t -> taskMapper.toCommentDto(t))
                        .collect(Collectors.toList());

    }


    private TaskComment addEmployeeComment(CommentAssignmentDTO dto , CommentByRole role){

        Task task = findTaskByTitle(dto.getTaskTitle());
        Users user = findUserByName(dto.getUserName());

        if (!isUserAssignedToTask(user , task)){
            throw new RuntimeException("User is not assigned to this task");
        }

        TaskComment comment = new TaskComment();
        comment.setCommentedByUser(user);
        comment.setCommentContent(dto.getCommentContent());
        comment.setCommentByRole(role);
        comment.setTasks(task);

        return commentRepository.save(comment);
    }


    private List<TaskComment> filterCommentsByEmployee(String taskTitle , String employeeName , CommentByRole role){

        Task task = findTaskByTitle(taskTitle);
        Users user = findUserByName(employeeName);

        if (!isUserAssignedToTask(user,task)){
            return List.of();
        }

        return commentRepository
                .findAllCommentByCommentRole(role)
                .stream()
                .filter(comment ->
                        comment.getCommentedByUser().getName().equalsIgnoreCase(employeeName) &&
                        comment.getTasks().getTaskTitle().equalsIgnoreCase(taskTitle)
                ).collect(Collectors.toList());
    }


    private Users findUserByName(String name){
        return userRepository.findUserByNameContaining(name)
                .orElseThrow(() -> new RuntimeException("User not found: " + name));
    }


    private Task findTaskByTitle(String title){
        return taskRepository.findByTaskTitle(title)
                .orElseThrow(() -> new RuntimeException("Task not found: " + title));
    }


    private void ensureTaskExists(String taskTitle){
        if (!taskRepository.findByTaskTitle(taskTitle).isPresent()){
            throw new RuntimeException("Task not found: " + taskTitle);
        }
    }


    private boolean isUserAssignedToTask(Users user , Task task){

        return (task.getAssignedTo() != null || task.getPreviouslyAssignedTo() != null ) &&
                (task.getAssignedTo().getName().equalsIgnoreCase(user.getName()) || task.getPreviouslyAssignedTo().getName().equalsIgnoreCase(user.getName()) );
    }

}

