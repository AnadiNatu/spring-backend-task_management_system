package com.newSystem.TaskManagementSystemImplemented.entity;

import com.newSystem.TaskManagementSystemImplemented.enums.CommentByRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_comments")
public class TaskComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commentContent;

    @Enumerated(EnumType.STRING)
    private CommentByRole commentByRole;

    @ManyToOne
    @JoinColumn(name = "commented_by_user_id")
    private Users commentedByUser;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task tasks;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = new Date();
    }

    @Override
    public String toString() {
        return "TaskComments{" +
                "id=" + id +
                ", commentContent='" + commentContent + '\'' +
                ", commentByRole=" + commentByRole +
                ", commentedByUser=" + commentedByUser +
                ", tasks=" + tasks +
                ", createdAt=" + createdAt +
                ", updateAt=" + updatedAt +
                '}';
    }
}

