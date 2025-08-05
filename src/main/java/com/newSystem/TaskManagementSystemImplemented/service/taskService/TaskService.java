package com.newSystem.TaskManagementSystemImplemented.service.taskService;


import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.*;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.UsersDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import com.newSystem.TaskManagementSystemImplemented.exception.ActionForRoleNotAuthorizedException;
import com.newSystem.TaskManagementSystemImplemented.exception.InvalidActionException;
import com.newSystem.TaskManagementSystemImplemented.exception.ResourceNotFoundException;
import com.newSystem.TaskManagementSystemImplemented.mapper.TaskUserNewMapper;
import com.newSystem.TaskManagementSystemImplemented.repository.TaskRepository;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import com.newSystem.TaskManagementSystemImplemented.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository tasksRepository;
    private final TaskUserNewMapper taskMapper;
    private final UserRepository usersRepository;
    private final JwtUtils jwtUtil;

    public Task createTask(CreateTaskDTO createTaskDTO) {
        Users fromUser = usersRepository.findUserByUsername(createTaskDTO.getAssignedFrom())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned from user not found: " + createTaskDTO.getAssignedFrom()));

        if (!userRoleAdminOrEmployer(fromUser)) {
            throw new ActionForRoleNotAuthorizedException("Only ADMIN or EMPLOYER can create tasks.");
        }

        Task task = new Task();
        task.setTaskTitle(createTaskDTO.getTaskTitle());
        task.setTaskDescription(createTaskDTO.getTaskDescription());
        task.setAssignedFrom(fromUser);
        task.setTaskStatus(createTaskDTO.getTaskStatus());
        task.setTaskCreatedOn(new Date());

        return tasksRepository.save(task);

    }

    public TasksDTO assigningCreatedTask(TaskAssignmentDTO dto) {
        Task task = tasksRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + dto.getTaskId()));

        Users fromUser = usersRepository.findUserByUsername(dto.getAssignedFrom())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned from user not found: " + dto.getAssignedFrom()));

        Users toUser = usersRepository.findUserByUsername(dto.getAssignedTo())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned to user not found: " + dto.getAssignedTo()));

        if (!task.getTaskTitle().equalsIgnoreCase(dto.getTaskTitle())) {
            throw new InvalidActionException("Task title mismatch.");
        }

        if (!userRoleAdminOrEmployer(fromUser)) {
            throw new ActionForRoleNotAuthorizedException("Only ADMIN or EMPLOYER can assign tasks.");
        }

        task.setAssignedFrom(fromUser);
        task.setAssignedTo(toUser);
        task.setAssignedOn(new Date());
        task.setTaskStatus(TaskStatus.ASSIGNED);
        toUser.setTaskAssignment(true);

        tasksRepository.save(task);
        return taskMapper.toDTO(task);

    }

    public AllUserAndTaskDetailDTO reassigningCreatedTask(TaskReassignmentDTO dto) {
        Task task = tasksRepository.findByTaskTitle(dto.getTaskTitle())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + dto.getTaskId()));

        Users fromUser = usersRepository.findUserByUsername(dto.getAssignedFrom())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned from user not found: " + dto.getAssignedFrom()));

        Users toUser = usersRepository.findUserByUsername(dto.getAssignedTo())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned to user not found: " + dto.getAssignedTo()));

        Users previouslyToUser = usersRepository.findUserByUsername(dto.getPreviouslyAssignedTo())
                .orElseThrow(() -> new ResourceNotFoundException("Previously assigned to user not found: " + dto.getPreviouslyAssignedTo()));

        if (!task.getTaskTitle().equalsIgnoreCase(dto.getTaskTitle())) {
            throw new InvalidActionException("Task title mismatch.");
        }

        if (!userRoleAdminOrEmployer(fromUser) && !(userRoleAdminOrEmployee(toUser)  || userRoleAdminOrEmployee(previouslyToUser))) {
            throw new ActionForRoleNotAuthorizedException("Only ADMIN or EMPLOYER can reassign tasks.");
        }

        task.setAssignedFrom(fromUser);
        task.setAssignedTo(toUser);
        task.setPreviouslyAssignedTo(previouslyToUser);
        task.setAssignedOn(dto.getAssignedOn());
        task.setTaskStatus(TaskStatus.TRANSFER);

        tasksRepository.save(task);
        return taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task);
    }

    public AllUserAndTaskDetailDTO taskStatusUpdate(TaskStatusUpdateDTO dto) {
        Task task = tasksRepository.findByTaskTitle(dto.getTaskTitle())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with title: " + dto.getTaskTitle()));

        TaskStatus newStatus;
        try {

            Users users = jwtUtil.getLoggedInUser();
            if (!userRoleAdminOrEmployer(users)){
                throw new ActionForRoleNotAuthorizedException("Employee can not access this");
            }

            newStatus = taskMapper.mapStringToTaskStatus(dto.getTaskStatus());
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Invalid task status: " + dto.getTaskStatus());
        }

        if (task.getTaskStatus() == TaskStatus.COMPLETED) {
            throw new InvalidActionException("Completed task status cannot be modified.");
        }

        task.setTaskStatus(newStatus);
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(new Date());
            if (!(task.getAssignedTo() == null)){
                Users usersAssigned = usersRepository.findUserByNameContaining(task.getAssignedTo().getName()).orElseThrow(() -> new ResourceNotFoundException("Assigned User Was Not Found"));
                if (usersAssigned.getCompleteTask() == 0){
                    usersAssigned.setCompleteTask(1);
                    usersRepository.save(usersAssigned);
                }else {
                    int count = usersAssigned.getCompleteTask();
                    count+=1;
                    usersAssigned.setCompleteTask(count);
                    usersRepository.save(usersAssigned);
                }
            }
        }

        Task savedTask = tasksRepository.save(task);
        return taskMapper.mapForUserTaskInfo(taskMapper.toDTO(savedTask), savedTask);
    }

    public List<AllUserAndTaskDetailDTO> getAllTaskByAssignedFrom(String assignedFrom) {
//        String user = jwtUtil.getLoggedInUser().getName()
        List<Task> tasks = tasksRepository.findAllByAssignedFrom(assignedFrom);

        Users users = jwtUtil.getLoggedInUser();
        if (!userRoleAdminOrEmployee(users)){
            throw new ActionForRoleNotAuthorizedException("Employer can not access this");
        }

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found assigned from user: " + assignedFrom);
        }

        return tasks.stream()
                .map(task -> taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task))
                .collect(Collectors.toList());
    }

    public List<AllUserAndTaskDetailDTO> getAllTaskByAssignedTo(String assignedTo) {
        List<Task> tasks = tasksRepository.findAllByAssignedTo(assignedTo);

        Users users = jwtUtil.getLoggedInUser();
        if (!userRoleAdminOrEmployee(users)){
            throw new ActionForRoleNotAuthorizedException("Employer can not access this");
        }

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found assigned to user: " + assignedTo);
        }

        return tasks.stream()
                .map(task -> taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task))
                .collect(Collectors.toList());
    }

    public List<AllUserAndTaskDetailDTO> getAllTaskByPreviouslyAssignedTo(String previouslyAssignedTo) {
        List<Task> tasks = tasksRepository.findAllByPreviousLyAssignedTo(previouslyAssignedTo);

        Users users = jwtUtil.getLoggedInUser();
        if (!userRoleAdminOrEmployee(users)){
            throw new ActionForRoleNotAuthorizedException("Employer can not access this");
        }

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found for previously assigned user: " + previouslyAssignedTo);
        }

        return tasks.stream()
                .map(task -> taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task))
                .collect(Collectors.toList());
    }

    public List<AllUserAndTaskDetailDTO> getTaskByAssignedUserAndStatus(String userName, String taskStatus) {
        TaskStatus status;
        try {
            Users users = jwtUtil.getLoggedInUser();
            if (!userRoleAdminOrEmployer(users)){
                throw new ActionForRoleNotAuthorizedException("Employee can not access this");
            }
            status = taskMapper.mapStringToTaskStatus(taskStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Invalid task status: " + taskStatus);
        }

        List<Task> tasks = tasksRepository.findAllByTaskStatus(status).stream()
                .filter(task -> task.getAssignedTo() != null &&
                        task.getAssignedTo().getName().equalsIgnoreCase(userName))
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found for user: " + userName + " with status: " + taskStatus);
        }

        return tasks.stream()
                .map(task -> taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task))
                .collect(Collectors.toList());
    }

    public List<AllUserAndTaskDetailDTO> getTaskByStatusBetweenDates(Date startDate, Date endDate, String taskStatus) {
        TaskStatus status;
        try {
            status = taskMapper.mapStringToTaskStatus(taskStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Invalid task status: " + taskStatus);
        }

        List<Task> tasks = tasksRepository.findAllByTaskStatus(status).stream()
                .filter(task -> {
                    Date created = task.getTaskCreatedOn();
                    return created != null && !created.before(startDate) && !created.after(endDate);
                }).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found between the specified dates with status: " + taskStatus);
        }

        return tasks.stream()
                .map(task -> taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task))
                .collect(Collectors.toList());
    }

    public AllUserAndTaskDetailDTO getTaskByTaskTitle(String taskTitle) {
        Users users = jwtUtil.getLoggedInUser();
        if (!userRoleAdminOrEmployee(users)){
            throw new ActionForRoleNotAuthorizedException("Employer can not access this");
        }

        Task task = tasksRepository.findByTaskTitle(taskTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with title: " + taskTitle));
        return taskMapper.mapForUserTaskInfo(taskMapper.toDTO(task), task);
    }

    public String deleteTaskAfterCompletion() {
        List<Task> completedTasks = tasksRepository.findAllByTaskStatus(TaskStatus.COMPLETED);

        if (completedTasks.isEmpty()) {
            throw new ResourceNotFoundException("No completed tasks found to delete.");
        }

        tasksRepository.deleteAll(completedTasks);
        return completedTasks.size() + " completed tasks deleted.";
    }

    public TasksDTO getTaskById(Long id){
        Task task = tasksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        return taskMapper.toDTO(task);
    }


    public List<TasksDTO> getTaskByDepartment(String department){
        List<Task> tasks = tasksRepository
                .findAll()
                .stream()
                .filter(t -> t.getAssignedTo() != null && department.equalsIgnoreCase(t.getAssignedTo().getDepartment()))
                .collect(Collectors.toList());

        if (tasks.isEmpty()){
            throw new RuntimeException("No tasks found for department: " + department);
        }

        return taskMapper.toDTOList(tasks);
    }

    public List<TasksDTO> getTasksAssignedBetweenDate(Date start , Date end){

        List<Task> tasks = tasksRepository
                .findAll()
                .stream()
                .filter(t -> t.getAssignedOn() != null &&
                        !t.getAssignedOn().before(start) &&
                        !t.getAssignedOn().after(end))
                .collect(Collectors.toList());

        if (tasks.isEmpty()){
            throw new ResourceNotFoundException("No tasks assigned between " + start + " and " + end);
        }

        return taskMapper.toDTOList(tasks);
    }

    public TasksDTO updateTaskStatus(TaskStatusUpdateDTO dto){

        Task task = tasksRepository.findByTaskTitle(dto.getTaskTitle()).orElseThrow(() -> new ResourceNotFoundException("Task not found with title: " + dto.getTaskTitle()));

        TaskStatus newStatus = taskMapper.mapStringToTaskStatus(dto.getTaskStatus());
        task.setTaskStatus(newStatus);
        task.setCompletedAt(TaskStatus.COMPLETED.equals(newStatus) ? new Date() : null);

        return taskMapper.toDTO(tasksRepository.save(task));
    }

//    public TasksDTO getTaskByTitle(String title){
//        Task task = tasksRepository.findByTaskTitle(title)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found with title: " + title));
//
//        return taskMapper.toDTO(task);
//    }

    public TasksDTO getTaskByAssignedToAndAssignedFrom(String assignedFrom){
        Users users = jwtUtil.getLoggedInUser();
        Task task;
        Users assignedFromUser = usersRepository.findUserByNameContaining(assignedFrom).orElseThrow(() -> new RuntimeException("User was not found"));
        if (userRoleAdminOrEmployee(users) && userRoleAdminOrEmployer(assignedFromUser)) {
            task = tasksRepository.findByAssignedToAndAssignedFrom(users.getName() , assignedFrom).orElseThrow(() -> new RuntimeException("Task was not found"));
            return taskMapper.toDTO(task);
        }
        return null;
    }

    public List<String> getAllUsers(){
        return usersRepository.findAll().stream().map(Users::getName).collect(Collectors.toList());
    }

    public List<String> getAllDepartments(){
        return tasksRepository.findAll().stream().map(Task::getAssignedFrom).map(Users :: getDepartment).distinct().collect(Collectors.toList());
    }


    private boolean userRoleAdminOrEmployee(Users users) {
        return users != null &&
                (users.getUserRoles().equals(UserRoles.ADMIN) ||
                        users.getUserRoles().equals(UserRoles.EMPLOYEE));
    }

    private boolean userRoleAdminOrEmployer(Users users) {
        return users != null &&
                (users.getUserRoles().equals(UserRoles.ADMIN) ||
                        users.getUserRoles().equals(UserRoles.EMPLOYER));
    }
}
