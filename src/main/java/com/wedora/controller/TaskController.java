package com.wedora.controller;

import com.wedora.dto.TaskDTO;
import com.wedora.service.TaskService;
import com.wedora.service.WeddingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Task management endpoints.
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final WeddingService weddingService;

    @Autowired
    public TaskController(TaskService taskService, WeddingService weddingService) {
        this.taskService = taskService;
        this.weddingService = weddingService;
    }

    @GetMapping
    public String listTasks(Model model) {
        try {
            List<TaskDTO> tasks = taskService.getAllTasks();
            List<TaskDTO> overdueTasks = taskService.getOverdueTasks();
            
            model.addAttribute("tasks", tasks);
            model.addAttribute("overdueTasks", overdueTasks);
            return "tasks/list";
        } catch (IOException e) {
            model.addAttribute("error", "Unable to load task data: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("task", new TaskDTO());
            model.addAttribute("weddings", weddingService.getAllWeddings());
            return "tasks/form";
        } catch (IOException e) {
            model.addAttribute("error", "Unable to load form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/new")
    public String createTask(
            @Valid @ModelAttribute("task") TaskDTO taskDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "tasks/form";
        }

        try {
            taskService.createTask(taskDTO);
            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
            return "redirect:/tasks";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save task. Please try again.");
            return "redirect:/tasks/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TaskDTO task = taskService.getTaskById(id);
            model.addAttribute("task", task);
            model.addAttribute("weddings", weddingService.getAllWeddings());
            return "tasks/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateTask(
            @PathVariable String id,
            @Valid @ModelAttribute("task") TaskDTO taskDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "tasks/form";
        }

        try {
            taskDTO.setId(id);
            taskService.updateTask(id, taskDTO);
            redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
            return "redirect:/tasks";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update task. Please try again.");
            return "redirect:/tasks/edit/" + id;
        }
    }

    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            taskService.completeTask(id);
            redirectAttributes.addFlashAttribute("success", "Task marked as completed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tasks";
    }
}
