package com.wedora.service;

import com.wedora.dto.ExpenseDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Expense;
import com.wedora.repository.BookingRepository;
import com.wedora.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Finance management.
 * Uses BigDecimal for precise monetary calculations.
 */
@Service
public class FinanceService {

    private final ExpenseRepository expenseRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public FinanceService(ExpenseRepository expenseRepository, BookingRepository bookingRepository) {
        this.expenseRepository = expenseRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Gets all expenses.
     */
    public List<ExpenseDTO> getAllExpenses() throws IOException {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets expenses by wedding ID.
     */
    public List<ExpenseDTO> getExpensesByWeddingId(String weddingId) throws IOException {
        List<Expense> expenses = expenseRepository.findByWeddingId(weddingId);
        return expenses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets expense by ID.
     */
    public ExpenseDTO getExpenseById(String id) throws IOException {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return toDTO(expense);
    }

    /**
     * Creates a new expense.
     */
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) throws IOException {
        Expense expense = toEntity(expenseDTO);
        expenseRepository.save(expense);
        return toDTO(expense);
    }

    /**
     * Updates an existing expense.
     */
    public ExpenseDTO updateExpense(String id, ExpenseDTO expenseDTO) throws IOException {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        existingExpense.setWeddingId(expenseDTO.getWeddingId());
        existingExpense.setDescription(expenseDTO.getDescription());
        existingExpense.setAmount(expenseDTO.getAmount());
        existingExpense.setExpenseDate(expenseDTO.getExpenseDate());
        existingExpense.setCategory(expenseDTO.getCategory());
        existingExpense.setNotes(expenseDTO.getNotes());

        expenseRepository.save(existingExpense);
        return toDTO(existingExpense);
    }

    /**
     * Deletes an expense.
     */
    public void deleteExpense(String id) throws IOException {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        expenseRepository.delete(id);
    }

    /**
     * Gets total expenses for a wedding.
     */
    public BigDecimal getTotalExpenses(String weddingId) throws IOException {
        return expenseRepository.getTotalExpensesByWedding(weddingId);
    }

    /**
     * Gets total bookings value for a wedding.
     */
    public BigDecimal getTotalBookings(String weddingId) throws IOException {
        return bookingRepository.findByWeddingId(weddingId).stream()
                .filter(b -> b.getStatus() == com.wedora.model.Booking.BookingStatus.CONFIRMED ||
                           b.getStatus() == com.wedora.model.Booking.BookingStatus.COMPLETED)
                .map(b -> b.getPrice() != null ? b.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets remaining budget for a wedding.
     */
    public BigDecimal getRemainingBudget(String weddingId) throws IOException {
        BigDecimal totalBookings = getTotalBookings(weddingId);
        BigDecimal totalExpenses = getTotalExpenses(weddingId);
        return totalBookings.subtract(totalExpenses);
    }

    private Expense toEntity(ExpenseDTO dto) {
        Expense expense = new Expense(dto.getId());
        expense.setWeddingId(dto.getWeddingId());
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setCategory(dto.getCategory());
        expense.setNotes(dto.getNotes());
        return expense;
    }

    private ExpenseDTO toDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getWeddingId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getCategory(),
                expense.getNotes()
        );
    }
}
