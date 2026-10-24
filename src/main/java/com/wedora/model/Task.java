package com.wedora.model;

import java.time.LocalDate;

/**
 * Task entity for wedding-related tasks.
 */
public class Task extends BaseEntity {
    private String weddingId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskPriority priority;
    private TaskStatus status;
    private String assignedTo;

    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public Task() {
        super();
        this.priority = TaskPriority.MEDIUM;
        this.status = TaskStatus.TODO;
    }

    public Task(String id) {
        super(id);
        this.priority = TaskPriority.MEDIUM;
        this.status = TaskStatus.TODO;
    }

    // Getters and Setters
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
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status != null ? status : TaskStatus.TODO;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo != null ? assignedTo.trim() : null;
    }

    /**
     * Checks if task is overdue.
     */
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != TaskStatus.COMPLETED;
    }

    /**
     * Marks task as completed.
     */
    public void complete() {
        this.status = TaskStatus.COMPLETED;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }
}
