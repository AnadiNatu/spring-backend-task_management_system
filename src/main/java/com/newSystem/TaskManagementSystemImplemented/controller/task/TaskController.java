package com.newSystem.TaskManagementSystemImplemented.controller.task;

import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.*;
import com.newSystem.TaskManagementSystemImplemented.dto.authenticationDto.UsersDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.repository.UserRepository;
import com.newSystem.TaskManagementSystemImplemented.service.authService.AuthService;
import com.newSystem.TaskManagementSystemImplemented.service.taskService.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<UsersDTO>> getAllUser(){
        List<UsersDTO> userList = authService.getAllTheUser();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userList);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UsersDTO> getUserById(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.getUserById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskDTO createTaskDTO) {
        Task createdTask = taskService.createTask(createTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PostMapping("/assign")
    public ResponseEntity<TasksDTO> assignTask(@RequestBody TaskAssignmentDTO dto) {
        TasksDTO result = taskService.assigningCreatedTask(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reassign")
    public ResponseEntity<AllUserAndTaskDetailDTO> reassignTask(@RequestBody TaskReassignmentDTO dto) {
        AllUserAndTaskDetailDTO result = taskService.reassigningCreatedTask(dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/status-update")
    public ResponseEntity<?> updateStatus(@RequestBody TaskStatusUpdateDTO dto) {
        AllUserAndTaskDetailDTO result = taskService.taskStatusUpdate(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/assigned-from/{username}")
    public ResponseEntity<?> getTasksByAssignedFrom(@PathVariable String username) {
        return ResponseEntity.ok(taskService.getAllTaskByAssignedFrom(username));
    }

    @GetMapping("/assigned-to/{username}")
    public ResponseEntity<?> getTasksByAssignedTo(@PathVariable String username) {
        return ResponseEntity.ok(taskService.getAllTaskByAssignedTo(username));
    }

    @GetMapping("/previously-assigned-to/{username}")
    public ResponseEntity<?> getTasksByPreviouslyAssignedTo(@PathVariable String username) {
        return ResponseEntity.ok(taskService.getAllTaskByPreviouslyAssignedTo(username));
    }

    @GetMapping("/title/{taskTitle}")
    public ResponseEntity<?> getTaskByTitle(@PathVariable String taskTitle) {
        return ResponseEntity.ok(taskService.getTaskByTaskTitle(taskTitle));
    }

    @DeleteMapping("/delete-completed")
    public ResponseEntity<String> deleteCompletedTasks() {
        String message = taskService.deleteTaskAfterCompletion();
        return ResponseEntity.ok(message);
    }

    @GetMapping("/user/status")
    public ResponseEntity<List<AllUserAndTaskDetailDTO>> getTasksByUserAnsStatus(@RequestParam("username") String username , @RequestParam("status") String status){

        List<AllUserAndTaskDetailDTO> tasks = taskService.getTaskByAssignedUserAndStatus(username , status);
        return ResponseEntity.ok(tasks);

    }

    @GetMapping("/status-between-dates")
    public ResponseEntity<List<AllUserAndTaskDetailDTO>> getTaskByStatusAndBetweenDates(@RequestParam("startDate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate , @RequestParam("endDate")@DateTimeFormat(pattern = "yyyy-MM-dd")Date endDate , @RequestParam("status")String status){

        List<AllUserAndTaskDetailDTO> tasks = taskService.getTaskByStatusBetweenDates(startDate, endDate, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TasksDTO> getTaskById(@PathVariable Long id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

   @GetMapping("/department/{department}")
    public ResponseEntity<List<TasksDTO>> getTaskByDepartment(@PathVariable String department){
        return ResponseEntity.ok(taskService.getTaskByDepartment(department));
   }

   @GetMapping("/assignedDate")
    public ResponseEntity<List<TasksDTO>> getTasksAssignedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date start ,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date end ){
        return ResponseEntity.ok(taskService.getTasksAssignedBetweenDate(start, end));
   }

   @PutMapping("/update-status")
   public ResponseEntity<TasksDTO> updateTaskStatus(@RequestBody TaskStatusUpdateDTO dto){
        return ResponseEntity.ok(taskService.updateTaskStatus(dto));
   }

//   @GetMapping("/getTaskByTitle/{taskTitle}")
//    public ResponseEntity<TasksDTO> getTaskByTitle(@PathVariable(name = "taskTitle")String title){
//        return ResponseEntity.ok(taskService.getTaskByTitle(title));
//   }

    @GetMapping("/assignedTo/assignedFrom/{assignedFrom}")
    public ResponseEntity<TasksDTO> getTaskByAssignedToAndAssignedFrom(@PathVariable(name = "assignedFrom") String assignedFrom){
        return ResponseEntity.ok(taskService.getTaskByAssignedToAndAssignedFrom(assignedFrom));
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<String>> getAllUsers(){
        return ResponseEntity.ok(taskService.getAllUsers());
    }
}