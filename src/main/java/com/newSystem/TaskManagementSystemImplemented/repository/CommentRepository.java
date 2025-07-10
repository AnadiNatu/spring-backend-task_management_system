package com.newSystem.TaskManagementSystemImplemented.repository;

import com.newSystem.TaskManagementSystemImplemented.entity.TaskComment;
import com.newSystem.TaskManagementSystemImplemented.enums.CommentByRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<TaskComment, Long> {

    @Query("SELECT c FROM TaskComment c WHERE c.commentByRole = :commentByRole")
    List<TaskComment> findAllCommentByCommentRole(@Param("commentByRole") CommentByRole commentByRole);

    @Query("SELECT c FROM TaskComment c JOIN c.commentedByUser u WHERE LOWER(u.name) = LOWER(:commentByUserName)")
    List<TaskComment> findAllCommentByUserName(@Param("commentByUserName") String commentByUserName);

    @Query("SELECT c FROM TaskComment c JOIN c.tasks t WHERE LOWER(t.taskTitle) LIKE LOWER(CONCAT('%' , :taskTitle , '%'))")
    List<TaskComment> findAllCommentByTaskTitle(@Param("taskTitle") String taskTitle);

    @Query("SELECT c FROM TaskComment c JOIN c.tasks t JOIN c.commentedByUser u WHERE " +
    "(:firstDate IS NULL OR c.createdAt >= :firstDate) AND" +
    "(:secondDate IS NULL OR c.createdAt <= :secondDate) AND" +
    "(:taskTitle IS NULL OR LOWER(t.taskTitle) LIKE LOWER(CONCAT('%' , :taskTitle , '%'))) AND" +
    "(:name IS NULL OR LOWER(u.name) = LOWER(:name))")
    List<TaskComment> findAllCommentsOnTaskByUserBetweenDates(@Param("firstDate")Date firstDate , @Param("secondDate")Date secondDate , @Param("taskTitle")String taskTitle , @Param("name")String name);

    @Query("SELECT c FROM TaskComment c JOIN c.tasks t JOIN c.commentedByUser u WHERE " +
            "(:taskTitle IS NULL OR LOWER(t.taskTitle) LIKE LOWER(CONCAT('%' , :taskTitle , '%'))) AND" +
            "(:name IS NULL OR LOWER(u.name) = LOWER(:name)) ")
    List<TaskComment> getAllCommentsByUserAndTask(@Param("taskTitle")String taskTitle , @Param("name")String name);

}
