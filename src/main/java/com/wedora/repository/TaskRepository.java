package com.wedora.repository;

import com.wedora.model.Task;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Task persistence using TXT files.
 */
@Repository
public class TaskRepository {

    private static final String TASKS_FILE = "tasks.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Task> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(TASKS_FILE);
        List<Task> tasks = new ArrayList();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Task task = fromString(line);
                    tasks.add(task);
                } catch (Exception e) {
                    System.err.println("Error parsing task line: " + line);
                }
            }
        }

        return tasks;
    }

    public Optional<Task> findById(String id) throws IOException {
        List<Task> tasks = findAll();
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public List<Task> findByWeddingId(String weddingId) throws IOException {
        return findAll().stream()
                .filter(t -> t.getWeddingId().equals(weddingId))
                .collect(Collectors.toList());
    }

    public List<Task> findOverdue() throws IOException {
        return findAll().stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    public List<Task> findByStatus(Task.TaskStatus status) throws IOException {
        return findAll().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void save(Task task) throws IOException {
        List<Task> tasks = findAll();

        boolean exists = tasks.stream()
                .anyMatch(t -> t.getId().equals(task.getId()));

        if (exists) {
            tasks = tasks.stream()
                    .map(t -> t.getId().equals(task.getId()) ? task : t)
                    .collect(Collectors.toList());
        } else {
            tasks.add(task);
        }

        task.markAsUpdated();
        writeAll(tasks);
    }

    public void delete(String id) throws IOException {
        List<Task> tasks = findAll();
        tasks = tasks.stream()
                .filter(t -> !t.getId().equals(id))
                .collect(Collectors.toList());
        writeAll(tasks);
    }

    private void writeAll(List<Task> tasks) throws IOException {
        List<String> lines = tasks.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(TASKS_FILE, lines);
    }

    private String toString(Task task) {
        return String.join("|",
                task.getId(),
                task.getWeddingId(),
                escape(task.getTitle()),
                escape(task.getDescription()),
                task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : "",
                task.getPriority().name(),
                task.getStatus().name(),
                escape(task.getAssignedTo()),
                task.getCreatedAt().format(DATETIME_FORMATTER),
                task.getUpdatedAt().format(DATETIME_FORMATTER)
        );
    }

    private Task fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid task data format");
        }

        Task task = new Task(parts[0]);
        task.setWeddingId(parts[1]);
        task.setTitle(unescape(parts[2]));
        task.setDescription(unescape(parts[3]));
        task.setDueDate(parts[4].isEmpty() ? null : LocalDate.parse(parts[4], DATE_FORMATTER));
        task.setPriority(Task.TaskPriority.valueOf(parts[5]));
        task.setStatus(Task.TaskStatus.valueOf(parts[6]));
        task.setAssignedTo(unescape(parts[7]));
        task.setCreatedAt(java.time.LocalDateTime.parse(parts[8], DATETIME_FORMATTER));
        task.setUpdatedAt(java.time.LocalDateTime.parse(parts[9], DATETIME_FORMATTER));

        return task;
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("|", "||");
    }

    private String unescape(String value) {
        if (value == null || value.isEmpty()) return null;
        return value.replace("||", "|");
    }
}
