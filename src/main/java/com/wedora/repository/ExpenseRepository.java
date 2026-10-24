package com.wedora.repository;

import com.wedora.model.Expense;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Expense persistence using TXT files.
 */
@Repository
public class ExpenseRepository {

    private static final String EXPENSES_FILE = "expenses.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Expense> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(EXPENSES_FILE);
        List<Expense> expenses = new ArrayList();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Expense expense = fromString(line);
                    expenses.add(expense);
                } catch (Exception e) {
                    System.err.println("Error parsing expense line: " + line);
                }
            }
        }

        return expenses;
    }

    public Optional<Expense> findById(String id) throws IOException {
        List<Expense> expenses = findAll();
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public List<Expense> findByWeddingId(String weddingId) throws IOException {
        return findAll().stream()
                .filter(e -> e.getWeddingId().equals(weddingId))
                .collect(Collectors.toList());
    }

    public void save(Expense expense) throws IOException {
        List<Expense> expenses = findAll();

        boolean exists = expenses.stream()
                .anyMatch(e -> e.getId().equals(expense.getId()));

        if (exists) {
            expenses = expenses.stream()
                    .map(e -> e.getId().equals(expense.getId()) ? expense : e)
                    .collect(Collectors.toList());
        } else {
            expenses.add(expense);
        }

        expense.markAsUpdated();
        writeAll(expenses);
    }

    public void delete(String id) throws IOException {
        List<Expense> expenses = findAll();
        expenses = expenses.stream()
                .filter(e -> !e.getId().equals(id))
                .collect(Collectors.toList());
        writeAll(expenses);
    }

    public BigDecimal getTotalExpensesByWedding(String weddingId) throws IOException {
        return findByWeddingId(weddingId).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void writeAll(List<Expense> expenses) throws IOException {
        List<String> lines = expenses.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(EXPENSES_FILE, lines);
    }

    private String toString(Expense expense) {
        return String.join("|",
                expense.getId(),
                expense.getWeddingId(),
                escape(expense.getDescription()),
                expense.getAmount().toString(),
                expense.getExpenseDate().format(DATE_FORMATTER),
                escape(expense.getCategory()),
                escape(expense.getNotes()),
                expense.getCreatedAt().format(DATETIME_FORMATTER),
                expense.getUpdatedAt().format(DATETIME_FORMATTER)
        );
    }

    private Expense fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 9) {
            throw new IllegalArgumentException("Invalid expense data format");
        }

        Expense expense = new Expense(parts[0]);
        expense.setWeddingId(parts[1]);
        expense.setDescription(unescape(parts[2]));
        expense.setAmount(new BigDecimal(parts[3]));
        expense.setExpenseDate(LocalDate.parse(parts[4], DATE_FORMATTER));
        expense.setCategory(unescape(parts[5]));
        expense.setNotes(unescape(parts[6]));
        expense.setCreatedAt(java.time.LocalDateTime.parse(parts[7], DATETIME_FORMATTER));
        expense.setUpdatedAt(java.time.LocalDateTime.parse(parts[8], DATETIME_FORMATTER));

        return expense;
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
