package com.wedora.controller;

import com.wedora.dto.StaffDTO;
import com.wedora.service.StaffService;
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
 * Controller for Staff management endpoints.
 */
@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String listStaff(Model model) throws IOException {
        List<StaffDTO> staffList = staffService.getAllStaff();
        model.addAttribute("staffList", staffList);
        return "staff/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new StaffDTO());
        return "staff/form";
    }

    @PostMapping("/new")
    public String createStaff(
            @Valid @ModelAttribute("staff") StaffDTO staffDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "staff/form";
        }

        try {
            staffService.createStaff(staffDTO);
            redirectAttributes.addFlashAttribute("success", "Staff member added successfully!");
            return "redirect:/staff";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save staff. Please try again.");
            return "redirect:/staff/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            StaffDTO staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            return "staff/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateStaff(
            @PathVariable String id,
            @Valid @ModelAttribute("staff") StaffDTO staffDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "staff/form";
        }

        try {
            staffDTO.setId(id);
            staffService.updateStaff(id, staffDTO);
            redirectAttributes.addFlashAttribute("success", "Staff member updated successfully!");
            return "redirect:/staff";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update staff. Please try again.");
            return "redirect:/staff/edit/" + id;
        }
    }

    @PostMapping("/archive/{id}")
    public String archiveStaff(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            staffService.archiveStaff(id);
            redirectAttributes.addFlashAttribute("success", "Staff member archived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff";
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("success", "Staff member deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff";
    }
}
