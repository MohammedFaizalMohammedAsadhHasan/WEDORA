package com.wedora.controller;

import com.wedora.dto.CustomerDTO;
import com.wedora.service.CustomerService;
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
 * Controller for Customer management endpoints.
 * Handles HTTP requests and returns Thymeleaf views.
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Displays list of all customers.
     */
    @GetMapping
    public String listCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String searchType,
            Model model) throws IOException {

        List<CustomerDTO> customers;

        if (search != null && !search.trim().isEmpty()) {
            if ("contact".equals(searchType)) {
                customers = customerService.searchByContact(search);
                model.addAttribute("searchMessage", "Search results for contact: " + search);
            } else {
                customers = customerService.searchByName(search);
                model.addAttribute("searchMessage", "Search results for name: " + search);
            }
            model.addAttribute("search", search);
            model.addAttribute("searchType", searchType);
        } else {
            customers = customerService.getActiveCustomers();
        }

        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", customerService.getCustomerCount());
        model.addAttribute("activeCustomers", customerService.getActiveCustomerCount());

        return "customers/list";
    }

    /**
     * Displays form to create a new customer.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("isEdit", false);
        return "customers/form";
    }

    /**
     * Processes customer creation.
     */
    @PostMapping("/new")
    public String createCustomer(
            @Valid @ModelAttribute("customer") CustomerDTO customerDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "customers/form";
        }

        try {
            customerService.createCustomer(customerDTO);
            redirectAttributes.addFlashAttribute("success", "Customer created successfully!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customers/new";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save customer. Please try again.");
            return "redirect:/customers/new";
        }
    }

    /**
     * Displays form to edit an existing customer.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            model.addAttribute("isEdit", true);
            return "customers/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customers";
        }
    }

    /**
     * Processes customer update.
     */
    @PostMapping("/edit/{id}")
    public String updateCustomer(
            @PathVariable String id,
            @Valid @ModelAttribute("customer") CustomerDTO customerDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "customers/form";
        }

        try {
            customerDTO.setId(id);
            customerService.updateCustomer(id, customerDTO);
            redirectAttributes.addFlashAttribute("success", "Customer updated successfully!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customers/edit/" + id;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update customer. Please try again.");
            return "redirect:/customers/edit/" + id;
        }
    }

    /**
     * Displays customer details.
     */
    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            return "customers/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customers";
        }
    }

    /**
     * Deletes (archives) a customer.
     */
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer archived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customers";
    }
}
