package com.wedora.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expense entity for tracking wedding-related expenses.
 * Uses BigDecimal for precise monetary calculations.
 */
public class Expense extends BaseEntity {
    private String weddingId;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String category;
    private String notes;

    public Expense() {
        super();
    }

    public Expense(String id) {
        super(id);
    }

    // Getters and Setters with validation
    public String getWeddingId() {
        return weddingId;
    }

    public void setWeddingId(String weddingId) {
        if (weddingId == null || weddingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Wedding ID cannot be empty");
        }
        this.weddingId = weddingId.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        if (expenseDate == null) {
            throw new IllegalArgumentException("Expense date cannot be null");
        }
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : null;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : null;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + getId() + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }
}
