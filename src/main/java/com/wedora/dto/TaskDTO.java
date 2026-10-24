package com.wedora.dto;

import com.wedora.model.Task;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Data Transfer Object for Task operations.
 */
public class TaskDTO {
    private String id;

    @NotBlank(message = "Wedding ID is required")
    private String weddingId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    private Task.TaskPriority priority;

    private Task.TaskStatus status;

    private String assignedTo;

    public TaskDTO() {
        this.priority = Task.TaskPriority.MEDIUM;
        this.status = Task.TaskStatus.TODO;
    }

    public TaskDTO(String id, String weddingId, String title, String description,
                   LocalDate dueDate, Task.TaskPriority priority, Task.TaskStatus status, String assignedTo) {
        this.id = id;
        this.weddingId = weddingId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeddingId() {
        return weddingId;
    }

    public void setWeddingId(String weddingId) {
        this.weddingId = weddingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Task.TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(Task.TaskPriority priority) {
        this.priority = priority;
    }

    public Task.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
