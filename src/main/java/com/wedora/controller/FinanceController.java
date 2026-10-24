package com.wedora.controller;

import com.wedora.dto.ExpenseDTO;
import com.wedora.service.FinanceService;
import com.wedora.service.WeddingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Finance management endpoints.
 */
@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;
    private final WeddingService weddingService;

    @Autowired
    public FinanceController(FinanceService financeService, WeddingService weddingService) {
        this.financeService = financeService;
        this.weddingService = weddingService;
    }

    /**
     * Displays finance overview for all weddings.
     */
    @GetMapping
    public String financeOverview(Model model) throws IOException {
        List<com.wedora.dto.WeddingDTO> weddings = weddingService.getAllWeddings();
        
        for (com.wedora.dto.WeddingDTO wedding : weddings) {
            BigDecimal totalExpenses = financeService.getTotalExpenses(wedding.getId());
            BigDecimal totalBookings = financeService.getTotalBookings(wedding.getId());
            BigDecimal remainingBudget = financeService.getRemainingBudget(wedding.getId());
            
            wedding.setTotalExpenses(totalExpenses);
            wedding.setTotalBookings(totalBookings);
            wedding.setRemainingBudget(remainingBudget);
        }
        
        model.addAttribute("weddings", weddings);
        return "finance/overview";
    }

    /**
     * Displays finance details for a specific wedding.
     */
    @GetMapping("/wedding/{weddingId}")
    public String weddingFinance(@PathVariable String weddingId, Model model, RedirectAttributes redirectAttributes) {
        try {
            com.wedora.dto.WeddingDTO wedding = weddingService.getWeddingById(weddingId);
            List<ExpenseDTO> expenses = financeService.getExpensesByWeddingId(weddingId);
            
            BigDecimal totalExpenses = financeService.getTotalExpenses(weddingId);
            BigDecimal totalBookings = financeService.getTotalBookings(weddingId);
            BigDecimal remainingBudget = financeService.getRemainingBudget(weddingId);
            
            model.addAttribute("wedding", wedding);
            model.addAttribute("expenses", expenses);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("totalBookings", totalBookings);
            model.addAttribute("remainingBudget", remainingBudget);
            
            return "finance/wedding";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/finance";
        }
    }

    /**
     * Displays form to add a new expense.
     */
    @GetMapping("/expense/new")
    public String showExpenseForm(@RequestParam String weddingId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("expense", new ExpenseDTO());
            model.addAttribute("weddingId", weddingId);
            model.addAttribute("wedding", weddingService.getWeddingById(weddingId));
            return "finance/expense-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/finance";
        }
    }

    /**
     * Processes expense creation.
     */
    @PostMapping("/expense/new")
    public String createExpense(
            @Valid @ModelAttribute("expense") ExpenseDTO expenseDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "finance/expense-form";
        }

        try {
            financeService.createExpense(expenseDTO);
            redirectAttributes.addFlashAttribute("success", "Expense added successfully!");
            return "redirect:/finance/wedding/" + expenseDTO.getWeddingId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save expense. Please try again.");
            return "redirect:/finance/expense/new?weddingId=" + expenseDTO.getWeddingId();
        }
    }

    /**
     * Deletes an expense.
     */
    @PostMapping("/expense/delete/{id}")
    public String deleteExpense(@PathVariable String id, @RequestParam String weddingId, RedirectAttributes redirectAttributes) {
        try {
            financeService.deleteExpense(id);
            redirectAttributes.addFlashAttribute("success", "Expense deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/finance/wedding/" + weddingId;
    }
}
