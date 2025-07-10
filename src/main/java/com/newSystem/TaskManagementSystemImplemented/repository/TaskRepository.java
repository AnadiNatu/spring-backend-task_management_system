package com.newSystem.TaskManagementSystemImplemented.repository;


import com.newSystem.TaskManagementSystemImplemented.entity.Task;
import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE LOWER(t.taskTitle) LIKE LOWER(CONCAT('%' , :taskTitle , '%'))")
    Optional<Task> findByTaskTitle(@Param("taskTitle") String title);

    @Query("SELECT t FROM Task t JOIN t.assignedFrom u WHERE LOWER(u.name) = LOWER(:assignedFrom)")
    List<Task> findAllByAssignedFrom(@Param("assignedFrom") String assignedFrom);

    @Query("SELECT t FROM Task t JOIN t.assignedFrom u  WHERE LOWER(u.name) = LOWER(:assignedFrom) AND LOWER(t.taskTitle) LIKE LOWER(CONCAT('%' , :taskTitle , '%'))")
    Optional<Task> findTaskByAssignedFromAndTaskTitle(@Param("assignedFrom") String assignedFrom , @Param("taskTitle") String taskTitle);

    @Query("SELECT t FROM Task t JOIN t.assignedTo u WHERE LOWER(u.name) = LOWER(:assignedTo)")
    List<Task> findAllByAssignedTo(@Param("assignedTo") String assignedTo);

    @Query("SELECT t FROM Task t  WHERE t.taskStatus = :taskStatus")
    List<Task> findAllByTaskStatus(@Param("taskStatus") TaskStatus taskStatus);

    @Query("SELECT t FROM Task t JOIN t.assignedTo atu JOIN t.assignedFrom afu WHERE LOWER(atu.name) = LOWER(:assignedTo) AND LOWER(afu.name) = LOWER(:assignedFrom)")
    Optional<Task> findByAssignedToAndAssignedFrom(@Param("assignedTo") String assignedTo , @Param("assignedFrom") String assignedFrom);

    @Query("SELECT t FROM Task t JOIN t.previouslyAssignedTo u WHERE LOWER(u.name) = LOWER(:previouslyAssignedToName)")
    List<Task> findAllByPreviousLyAssignedTo(@Param("previouslyAssignedToName") String previouslyAssignedTo);

}
