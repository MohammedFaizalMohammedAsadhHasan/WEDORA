package com.wedora.controller;

import com.wedora.dto.WeddingDTO;
import com.wedora.service.CustomerService;
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
 * Controller for Wedding management endpoints.
 */
@Controller
@RequestMapping("/weddings")
public class WeddingController {

    private final WeddingService weddingService;
    private final CustomerService customerService;

    @Autowired
    public WeddingController(WeddingService weddingService, CustomerService customerService) {
        this.weddingService = weddingService;
        this.customerService = customerService;
    }

    /**
     * Displays list of all weddings.
     */
    @GetMapping
    public String listWeddings(
            @RequestParam(required = false) String search,
            Model model) throws IOException {

        List<WeddingDTO> weddings;

        if (search != null && !search.trim().isEmpty()) {
            weddings = weddingService.searchByName(search);
            model.addAttribute("searchMessage", "Search results for: " + search);
            model.addAttribute("search", search);
        } else {
            weddings = weddingService.getAllWeddings();
        }

        model.addAttribute("weddings", weddings);
        model.addAttribute("totalWeddings", weddingService.getWeddingCount());
        model.addAttribute("upcomingWeddings", weddingService.getUpcomingWeddingCount());

        return "weddings/list";
    }

    /**
     * Displays form to create a new wedding.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) throws IOException {
        model.addAttribute("wedding", new WeddingDTO());
        model.addAttribute("customers", customerService.getActiveCustomers());
        model.addAttribute("isEdit", false);
        return "weddings/form";
    }

    /**
     * Processes wedding creation.
     */
    @PostMapping("/new")
    public String createWedding(
            @Valid @ModelAttribute("wedding") WeddingDTO weddingDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "weddings/form";
        }

        try {
            weddingService.createWedding(weddingDTO);
            redirectAttributes.addFlashAttribute("success", "Wedding created successfully!");
            return "redirect:/weddings";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save wedding. Please try again.");
            return "redirect:/weddings/new";
        }
    }

    /**
     * Displays form to edit an existing wedding.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            WeddingDTO wedding = weddingService.getWeddingById(id);
            model.addAttribute("wedding", wedding);
            model.addAttribute("customers", customerService.getActiveCustomers());
            model.addAttribute("isEdit", true);
            return "weddings/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/weddings";
        }
    }

    /**
     * Processes wedding update.
     */
    @PostMapping("/edit/{id}")
    public String updateWedding(
            @PathVariable String id,
            @Valid @ModelAttribute("wedding") WeddingDTO weddingDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "weddings/form";
        }

        try {
            weddingDTO.setId(id);
            weddingService.updateWedding(id, weddingDTO);
            redirectAttributes.addFlashAttribute("success", "Wedding updated successfully!");
            return "redirect:/weddings";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update wedding. Please try again.");
            return "redirect:/weddings/edit/" + id;
        }
    }

    /**
     * Displays wedding details.
     */
    @GetMapping("/{id}")
    public String viewWedding(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            WeddingDTO wedding = weddingService.getWeddingById(id);
            model.addAttribute("wedding", wedding);
            return "weddings/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/weddings";
        }
    }

    /**
     * Deletes a wedding.
     */
    @PostMapping("/delete/{id}")
    public String deleteWedding(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            weddingService.deleteWedding(id);
            redirectAttributes.addFlashAttribute("success", "Wedding deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/weddings";
    }

    /**
     * Cancels a wedding.
     */
    @PostMapping("/cancel/{id}")
    public String cancelWedding(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            weddingService.cancelWedding(id);
            redirectAttributes.addFlashAttribute("success", "Wedding cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/weddings";
    }
}
