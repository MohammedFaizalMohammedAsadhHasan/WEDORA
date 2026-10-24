package com.wedora.service;

import com.wedora.dto.TaskDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Task;
import com.wedora.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Task business logic.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getAllTasks() throws IOException {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByWeddingId(String weddingId) throws IOException {
        List<Task> tasks = taskRepository.findByWeddingId(weddingId);
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getOverdueTasks() throws IOException {
        List<Task> tasks = taskRepository.findOverdue();
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(String id) throws IOException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toDTO(task);
    }

    public TaskDTO createTask(TaskDTO taskDTO) throws IOException {
        Task task = toEntity(taskDTO);
        taskRepository.save(task);
        return toDTO(task);
    }

    public TaskDTO updateTask(String id, TaskDTO taskDTO) throws IOException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        existingTask.setWeddingId(taskDTO.getWeddingId());
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDueDate(taskDTO.getDueDate());
        existingTask.setPriority(taskDTO.getPriority());
        existingTask.setStatus(taskDTO.getStatus());
        existingTask.setAssignedTo(taskDTO.getAssignedTo());

        taskRepository.save(existingTask);
        return toDTO(existingTask);
    }

    public void completeTask(String id) throws IOException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        task.complete();
        taskRepository.save(task);
    }

    public void deleteTask(String id) throws IOException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(id);
    }

    private Task toEntity(TaskDTO dto) {
        Task task = new Task(dto.getId());
        task.setWeddingId(dto.getWeddingId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setAssignedTo(dto.getAssignedTo());
        return task;
    }

    private TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getWeddingId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getAssignedTo()
        );
    }
}
