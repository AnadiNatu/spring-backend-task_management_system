package com.newSystem.TaskManagementSystemImplemented.mapper;


import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.*;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.UsersDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.entity.TaskComment;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskUserNewMapper {

    private final UserRepository userRepository;

    public TasksDTO toDTO(Task tasks){

        if (tasks == null){
            return null;
        }

        TasksDTO dto = new TasksDTO();
        dto.setTaskIds(tasks.getTaskIds());
        dto.setTaskTitle(tasks.getTaskTitle());
        dto.setTaskDescription(tasks.getTaskDescription());
        dto.setTaskStatus(mapTaskStatusToString(tasks.getTaskStatus()));
        dto.setAssignedOn(tasks.getAssignedOn());
        dto.setCompletedAt(tasks.getCompletedAt());
        dto.setTaskCreatedOn(tasks.getTaskCreatedOn());

        if (tasks.getAssignedFrom() != null)dto.setAssignedToId(tasks.getAssignedFrom().getId());
        if (tasks.getAssignedTo() != null)dto.setAssignedFromId(tasks.getAssignedTo().getId());
        if (tasks.getPreviouslyAssignedTo() != null)dto.setPreviouslyAssignedToId(tasks.getPreviouslyAssignedTo().getId());


        return dto;
    }

    public List<TasksDTO> toDTOList(List<Task> tasksList){

        if (tasksList == null || tasksList.isEmpty()){
            return Collections.emptyList();
        }

        return tasksList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

    public UsersDTO getUsersDTO(Users createdUsers){

        UsersDTO userDTO = new UsersDTO();

        userDTO.setId(createdUsers.getId());
        userDTO.setName(createdUsers.getName());
        userDTO.setUsername(createdUsers.getUsername());
        userDTO.setPassword(new BCryptPasswordEncoder().encode(createdUsers.getPassword()));
        userDTO.setAge(createdUsers.getAge());
        userDTO.setDepartment(createdUsers.getDepartment());
        userDTO.setTaskAssignment(createdUsers.isTaskAssignment());
        userDTO.setCompleteTask(createdUsers.getCompleteTask());
        userDTO.setUserRoles(createdUsers.getUserRoles());
        userDTO.setTaskIds(getTaskId(new ArrayList<>()));

        return userDTO;
    }

    private List<Long> getTaskId(List<Task> tasks){
        return tasks.stream().map(Task::getTaskIds).collect(Collectors.toList());
    }

    public Task toEntity(TasksDTO dto){

        if (dto == null){
            return null;
        }

        Task tasks = new Task();
        tasks.setTaskIds(dto.getTaskIds());
        tasks.setTaskTitle(dto.getTaskTitle());
        tasks.setTaskDescription(dto.getTaskDescription());
        tasks.setTaskStatus(mapStringToTaskStatus(dto.getTaskStatus()));
        tasks.setAssignedOn(dto.getAssignedOn());
        tasks.setCompletedAt(dto.getCompletedAt());

        if (dto.getAssignedFromId() != null){
            Users fromUser = userRepository.findById(dto.getAssignedFromId()).orElseThrow(() -> new RuntimeException("Assigned from user not found"));
            tasks.setAssignedFrom(fromUser);
        }

        if (dto.getAssignedToId() != null){
            Users toUser = userRepository.findById(dto.getAssignedToId()).orElseThrow(() -> new RuntimeException("Assigned to user not found"));
            tasks.setAssignedFrom(toUser);
        }

        if (dto.getPreviouslyAssignedToId() != null){
            Users prevUser = userRepository.findById(dto.getPreviouslyAssignedToId()).orElseThrow(() -> new RuntimeException("Previously assigned to user not found"));
            tasks.setAssignedFrom(prevUser);
        }

        return tasks;
    }

    public AllUserAndTaskDetailDTO mapForUserTaskInfo(TasksDTO tasksDTO , Task tasks){

        AllUserAndTaskDetailDTO dto = new AllUserAndTaskDetailDTO();

        Optional<Users> fromUser = Optional.empty();
        Optional<Users> toUser = Optional.empty();
        Optional<Users> previouslyUser = Optional.empty();

        if (tasksDTO.getAssignedFromId() != null){
            fromUser = userRepository.findById(tasksDTO.getAssignedFromId());
        }

        if (tasksDTO.getAssignedToId() != null){
            toUser = userRepository.findById(tasksDTO.getAssignedToId());
        }

        if (tasksDTO.getPreviouslyAssignedToId() != null){
            previouslyUser = userRepository.findById(tasksDTO.getPreviouslyAssignedToId());
        }

        dto.setTaskId(tasks.getTaskIds());
        dto.setTaskTitle(tasks.getTaskTitle());
        dto.setTaskDescription(tasks.getTaskDescription());
        dto.setTaskStatus(mapTaskStatusToString(tasks.getTaskStatus()));
        dto.setTaskCreatedOn(tasks.getTaskCreatedOn());
        dto.setAssignedOn(tasks.getAssignedOn());
        dto.setCompletedAt(tasks.getCompletedAt());

        fromUser.ifPresent(u -> {
            dto.setAssignedFromId(u.getId());
            dto.setAssignedFromName(u.getName());
        });

        toUser.ifPresent(u -> {
            dto.setAssignedToId(u.getId());
            dto.setAssignedToName(u.getName());
        });

        previouslyUser.ifPresent(u -> {
            dto.setPreviouslyAssignedToId(u.getId());
            dto.setPreviouslyAssignedToName(u.getName());
        });

        return dto;
    }

    public TaskStatusUpdateDTO mapToStatusUpdateDto(Task task){

        TaskStatusUpdateDTO dto = new TaskStatusUpdateDTO();

        dto.setTaskId(task.getTaskIds());
        dto.setTaskTitle(task.getTaskTitle());
        dto.setTaskStatus(mapTaskStatusToString(task.getTaskStatus()));

        return dto;

    }

    public CommentDTO mapToCommentDto(TaskComment comment){

        CommentDTO dto = new CommentDTO();

        dto.setCommentContent(comment.getCommentContent());
        dto.setCommentByRole(comment.getCommentByRole());
        dto.setTaskTitle(comment.getTasks().getTaskTitle());
        dto.setTaskStatus(comment.getTasks().getTaskStatus());
        dto.setCommentByUserName(comment.getCommentedByUser().getName());
        dto.setUserRoles(comment.getCommentedByUser().getUserRoles());

        return dto;

    }

    public TaskAssignmentDTO taskAssignmentDTO(Task task , String comment){

        if (task == null)return null;

        TaskAssignmentDTO dto = new TaskAssignmentDTO();
        dto.setTaskId(task.getTaskIds());
        dto.setTaskTitle(task.getTaskTitle());
        dto.setTaskComment(comment);
        dto.setAssignedFrom(task.getAssignedFrom() != null ? task.getAssignedFrom().getName() : null);
        dto.setAssignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null);
        dto.setAssignedOn(task.getAssignedOn());

        return dto;
    }

    public TaskReassignmentDTO toReassignmentDto(Task task , String comment , Date previouslyAssignedOn){

        if (task == null)return null;

        TaskReassignmentDTO dto = new TaskReassignmentDTO();
        dto.setTaskId(task.getTaskIds());
        dto.setTaskTitle(task.getTaskTitle());
        dto.setTaskComment(comment);
        dto.setAssignedFrom(task.getAssignedFrom() != null ? task.getAssignedFrom().getName() : null);
        dto.setAssignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null);
        dto.setPreviouslyAssignedTo(task.getPreviouslyAssignedTo() != null ? task.getPreviouslyAssignedTo().getName() : null);
        dto.setAssignedOn(task.getAssignedOn());
        dto.setPreviouslyAssignedOn(previouslyAssignedOn);

        return dto;
    }

    public TaskStatusUpdateDTO toStatusUpdateDto(Task task){

        if (task == null)return null;

        TaskStatusUpdateDTO dto = new TaskStatusUpdateDTO();
        dto.setTaskId(task.getTaskIds());
        dto.setTaskTitle(task.getTaskTitle());
        dto.setTaskStatus(mapTaskStatusToString(task.getTaskStatus()));

        return dto;
    }

    public CommentDTO toCommentDto(TaskComment comment){

        if (comment == null)return null;

        CommentDTO dto = new CommentDTO();
        dto.setCommentContent(comment.getCommentContent());
        dto.setCommentByRole(comment.getCommentByRole());
        dto.setTaskTitle(comment.getTasks() != null ? comment.getTasks().getTaskTitle() : null);
        dto.setCommentByUserName(comment.getCommentedByUser() != null ? comment.getCommentedByUser().getName() : null);
        dto.setTaskStatus(comment.getTasks().getTaskStatus());
        dto.setUserRoles(comment.getCommentedByUser() != null ? comment.getCommentedByUser().getUserRoles() : null);

        return dto;

    }

    public UserTaskListDTO toUserTaskListDto(Users user){

        if (user == null)return null;

        UserTaskListDTO dto = new UserTaskListDTO();
        dto.setName(user.getName());
        dto.setListOfTask(user.getTask() != null ? user.getTask() : new ArrayList<>());

        return dto;

    }

    public List<TaskExcelDTO> toTaskExcelDTO(List<Task> taskList) {

        List<TaskExcelDTO> excelDTOList = new ArrayList<>();

        for (Task task : taskList) {

            TaskExcelDTO dto = new TaskExcelDTO();

            Optional<Users> fromUser = Optional.empty();
            Optional<Users> toUser = Optional.empty();
            Optional<Users> previouslyUser = Optional.empty();

            if (task.getAssignedFrom() != null && task.getAssignedFrom().getId() != null) {
                fromUser = userRepository.findById(task.getAssignedFrom().getId());
            }

            if (task.getAssignedTo() != null && task.getAssignedTo().getId() != null) {
                toUser = userRepository.findById(task.getAssignedTo().getId());
            }

            if (task.getPreviouslyAssignedTo() != null && task.getPreviouslyAssignedTo().getId() != null) {
                previouslyUser = userRepository.findById(task.getPreviouslyAssignedTo().getId());
            }

            dto.setTaskId(task.getTaskIds());
            dto.setTaskTitle(task.getTaskTitle());
            dto.setDepartment(task.getAssignedFrom().getDepartment());
            dto.setTaskStatus((task.getTaskStatus()));


            fromUser.ifPresent(u -> {
                dto.setAssignedFromUserId(u.getId());
                dto.setAssignedFromUserName(u.getName());
            });

            toUser.ifPresent(u -> {
                dto.setAssignedToUserId(u.getId());
                dto.setAssignedToUserName(u.getName());
            });

            previouslyUser.ifPresent(u -> {
                dto.setPreviouslyAssignedToUserId(u.getId());
                dto.setPreviouslyAssignedToUserName(u.getName());
            });

            excelDTOList.add(dto);
        }

        return excelDTOList;
    }

    public String mapTaskStatusToString(TaskStatus taskStatus){

        return (switch (taskStatus){
            case COMPLETED -> "Completely";
            case IN_PROGRESS -> "In_Progress";
            case ASSIGNED -> "Assigned";
            case TRANSFER -> "Transfer";
            default -> "Pending";
        }).toLowerCase();

    }

    public TaskStatus mapStringToTaskStatus(String taskStatus){

        return switch (taskStatus.toUpperCase()){

            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "ASSIGNED" -> TaskStatus.ASSIGNED;
            case "TRANSFER" -> TaskStatus.TRANSFER;
            default -> TaskStatus.PENDING;
        };
    }

    public UsersDTO toUserDto(Users user) {
        UsersDTO dto = new UsersDTO();
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setUserRoles(UserRoles.EMPLOYEE);
        dto.setId(user.getId());

        return dto;
    }
}
