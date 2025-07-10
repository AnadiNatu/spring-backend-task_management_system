package com.newSystem.TaskManagementSystemImplemented.controller.comment;


import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.CommentAssignmentDTO;
import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.CommentDTO;
import com.newSystem.TaskManagementSystemImplemented.dto.applicationDto.CreateTaskDTO;
import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.service.commentService.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin("*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskDTO dto , @RequestParam(required = false, name = "comment")String comment){
        return ResponseEntity.ok(commentService.createTaskWithOptionalComment(dto, comment));
    }

    @PostMapping("/assigned")
    public ResponseEntity<CommentDTO> addCommentAssigned(@RequestBody CommentAssignmentDTO dto){

        return ResponseEntity.ok(commentService.addEmployeeCommentOnAssignedTask(dto));

    }

    @PostMapping("/reassigned")
    public ResponseEntity<CommentDTO> addCommentReassigned(@RequestBody CommentAssignmentDTO dto){

        return ResponseEntity.ok(commentService.addEmployeeCommentOnReAssignedTask(dto));
    }

    @GetMapping("/byTitle/{taskTitle}")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByTask(@PathVariable String taskTitle){

        return ResponseEntity.ok(commentService.getAllCommentsByTaskTitle(taskTitle));

    }

    @GetMapping("/allAssigned")
    public ResponseEntity<List<CommentDTO>> getCommentsByAssignedEmployee(
            @RequestParam String taskTitle,
            @RequestParam String employeeName) {
        return ResponseEntity.ok(commentService.getCommentsOnTaskByEmployee(taskTitle, employeeName));
    }

    @GetMapping("/allReassigned")
    public ResponseEntity<List<CommentDTO>> getCommentsByReassignedEmployee(
            @RequestParam String taskTitle,
            @RequestParam String employeeName) {
//        Changing the return type to CommentDto
        return ResponseEntity.ok(commentService.getCommentsOnTaskByReassignedEmployee(taskTitle, employeeName));
    }

//    A @PAthVariable just after the @RequestMapping causes this error
    @GetMapping("/byId/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<CommentDTO>> getCommentByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(commentService.getCommentByUserName(userName));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CommentDTO>> getCommentByEmployeeBetweenDates(
            @RequestParam String taskTitle,
            @RequestParam String employeeName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        return ResponseEntity.ok(commentService.getCommentByEmployeeBetweenDates(taskTitle, employeeName, to, from));
    }

}
