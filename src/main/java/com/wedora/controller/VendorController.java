package com.wedora.controller;

import com.wedora.dto.VendorDTO;
import com.wedora.service.VendorService;
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
 * Controller for Vendor management endpoints.
 */
@Controller
@RequestMapping("/vendors")
public class VendorController {

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Displays list of all vendors.
     */
    @GetMapping
    public String listVendors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Model model) {

        try {
            List<VendorDTO> vendors;

            if (search != null && !search.trim().isEmpty()) {
                vendors = vendorService.searchByName(search);
                model.addAttribute("searchMessage", "Search results for: " + search);
                model.addAttribute("search", search);
            } else if (category != null && !category.isEmpty()) {
                vendors = vendorService.getVendorsByCategory(category);
                model.addAttribute("searchMessage", "Vendors in category: " + category);
                model.addAttribute("category", category);
            } else {
                vendors = vendorService.getActiveVendors();
            }

            model.addAttribute("vendors", vendors);
            model.addAttribute("totalVendors", vendorService.getVendorCount());
            model.addAttribute("activeVendors", vendorService.getActiveVendorCount());

            return "vendors/list";
        } catch (IOException e) {
            model.addAttribute("error", "Unable to load vendor data: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Displays form to create a new vendor.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("vendor", new VendorDTO());
            model.addAttribute("isEdit", false);
            return "vendors/form";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load form: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Processes vendor creation.
     */
    @PostMapping("/new")
    public String createVendor(
            @Valid @ModelAttribute("vendor") VendorDTO vendorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "vendors/form";
        }

        try {
            vendorService.createVendor(vendorDTO);
            redirectAttributes.addFlashAttribute("success", "Vendor created successfully!");
            return "redirect:/vendors";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save vendor. Please try again.");
            return "redirect:/vendors/new";
        }
    }

    /**
     * Displays form to edit an existing vendor.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            VendorDTO vendor = vendorService.getVendorById(id);
            model.addAttribute("vendor", vendor);
            model.addAttribute("isEdit", true);
            return "vendors/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/vendors";
        }
    }

    /**
     * Processes vendor update.
     */
    @PostMapping("/edit/{id}")
    public String updateVendor(
            @PathVariable String id,
            @Valid @ModelAttribute("vendor") VendorDTO vendorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "vendors/form";
        }

        try {
            vendorDTO.setId(id);
            vendorService.updateVendor(id, vendorDTO);
            redirectAttributes.addFlashAttribute("success", "Vendor updated successfully!");
            return "redirect:/vendors";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update vendor. Please try again.");
            return "redirect:/vendors/edit/" + id;
        }
    }

    /**
     * Displays vendor details.
     */
    @GetMapping("/{id}")
    public String viewVendor(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            VendorDTO vendor = vendorService.getVendorById(id);
            model.addAttribute("vendor", vendor);
            return "vendors/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/vendors";
        }
    }

    /**
     * Deletes (archives) a vendor.
     */
    @PostMapping("/delete/{id}")
    public String deleteVendor(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            vendorService.deleteVendor(id);
            redirectAttributes.addFlashAttribute("success", "Vendor archived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendors";
    }
}
