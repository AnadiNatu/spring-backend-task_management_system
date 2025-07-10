package com.newSystem.TaskManagementSystemImplemented.entity;


import com.newSystem.TaskManagementSystemImplemented.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskIds;
    private String taskTitle;
    private String taskDescription;

    @ManyToOne
    @JoinColumn(name = "assigned_from_id")
    private Users assignedFrom;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Users assignedTo;

    @ManyToOne
    @JoinColumn(name = "previously_assigned_to_id")
    private Users previouslyAssignedTo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date taskCreatedOn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedOn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @OneToMany(mappedBy = "tasks" , cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    private List<TaskComment> comments = new ArrayList<>();

    @Override
    public String toString() {
        return "Tasks{" +
                "taskIds=" + taskIds +
                ", taskTitle='" + taskTitle + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", assignedFrom=" + assignedFrom +
                ", assignedTo=" + assignedTo +
                ", previouslyAssignedTo=" + previouslyAssignedTo +
                ", taskCreatedOn=" + taskCreatedOn +
                ", assignedOn=" + assignedOn +
                ", completedAt=" + completedAt +
                ", taskStatus=" + taskStatus +
                '}';
    }
}

