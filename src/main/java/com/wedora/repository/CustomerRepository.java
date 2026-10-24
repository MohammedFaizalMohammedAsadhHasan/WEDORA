package com.wedora.repository;

import com.wedora.model.Customer;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Customer persistence using TXT files.
 * Demonstrates OOP concept: Abstraction
 * Hides file persistence details behind interface-like methods.
 * Can be easily replaced with database implementation later.
 */
@Repository
public class CustomerRepository {

    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Finds all customers.
     */
    public List<Customer> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(CUSTOMERS_FILE);
        List<Customer> customers = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Customer customer = fromString(line);
                    customers.add(customer);
                } catch (Exception e) {
                    // Skip corrupted lines
                    System.err.println("Error parsing customer line: " + line);
                }
            }
        }

        return customers;
    }

    /**
     * Finds customer by ID.
     */
    public Optional<Customer> findById(String id) throws IOException {
        List<Customer> customers = findAll();
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds active customers only.
     */
    public List<Customer> findActive() throws IOException {
        return findAll().stream()
                .filter(Customer::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Searches customers by name.
     */
    public List<Customer> searchByName(String searchTerm) throws IOException {
        String lowerTerm = searchTerm.toLowerCase();
        return findAll().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(lowerTerm))
                .collect(Collectors.toList());
    }

    /**
     * Searches customers by phone or email.
     */
    public List<Customer> searchByContact(String searchTerm) throws IOException {
        String lowerTerm = searchTerm.toLowerCase();
        return findAll().stream()
                .filter(c -> (c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerTerm)) ||
                             (c.getPhone() != null && c.getPhone().contains(searchTerm)))
                .collect(Collectors.toList());
    }

    /**
     * Saves a customer (create or update).
     */
    public void save(Customer customer) throws IOException {
        List<Customer> customers = findAll();

        // Check if customer exists
        boolean exists = customers.stream()
                .anyMatch(c -> c.getId().equals(customer.getId()));

        if (exists) {
            // Update existing
            customers = customers.stream()
                    .map(c -> c.getId().equals(customer.getId()) ? customer : c)
                    .collect(Collectors.toList());
        } else {
            // Add new
            customers.add(customer);
        }

        customer.markAsUpdated();
        writeAll(customers);
    }

    /**
     * Deletes a customer by ID (soft delete - archives instead).
     */
    public void delete(String id) throws IOException {
        Optional<Customer> customerOpt = findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.archive();
            save(customer);
        }
    }

    /**
     * Permanently deletes a customer (use with caution).
     */
    public void deletePermanent(String id) throws IOException {
        List<Customer> customers = findAll();
        customers = customers.stream()
                .filter(c -> !c.getId().equals(id))
                .collect(Collectors.toList());
        writeAll(customers);
    }

    /**
     * Counts total customers.
     */
    public long count() throws IOException {
        return findAll().size();
    }

    /**
     * Counts active customers.
     */
    public long countActive() throws IOException {
        return findActive().size();
    }

    /**
     * Writes all customers to file.
     */
    private void writeAll(List<Customer> customers) throws IOException {
        List<String> lines = customers.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(CUSTOMERS_FILE, lines);
    }

    /**
     * Converts Customer to string for file storage.
     * Format: id|fullName|email|phone|address|notes|active|createdAt|updatedAt
     */
    private String toString(Customer customer) {
        return String.join("|",
                customer.getId(),
                escape(customer.getFullName()),
                escape(customer.getEmail()),
                escape(customer.getPhone()),
                escape(customer.getAddress()),
                escape(customer.getNotes()),
                String.valueOf(customer.isActive()),
                customer.getCreatedAt().format(FORMATTER),
                customer.getUpdatedAt().format(FORMATTER)
        );
    }

    /**
     * Converts string from file to Customer.
     */
    private Customer fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 9) {
            throw new IllegalArgumentException("Invalid customer data format");
        }

        Customer customer = new Customer(parts[0]);
        customer.setFullName(unescape(parts[1]));
        customer.setEmail(unescape(parts[2]));
        customer.setPhone(unescape(parts[3]));
        customer.setAddress(unescape(parts[4]));
        customer.setNotes(unescape(parts[5]));
        customer.setActive(Boolean.parseBoolean(parts[6]));
        customer.setCreatedAt(java.time.LocalDateTime.parse(parts[7], FORMATTER));
        customer.setUpdatedAt(java.time.LocalDateTime.parse(parts[8], FORMATTER));

        return customer;
    }

    /**
     * Escapes pipe characters in field values.
     */
    private String escape(String value) {
        if (value == null) return "";
        return value.replace("|", "||");
    }

    /**
     * Unescapes pipe characters in field values.
     */
    private String unescape(String value) {
        if (value == null || value.isEmpty()) return null;
        return value.replace("||", "|");
    }
}
