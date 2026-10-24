package com.wedora.model;

import java.time.LocalDateTime;

/**
 * Customer entity representing a wedding business client.
 * Demonstrates OOP concept: Encapsulation
 * Private fields with controlled access through getters/setters.
 * Inherits common behavior from BaseEntity.
 */
public class Customer extends BaseEntity {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String notes;
    private boolean active;

    public Customer() {
        super();
        this.active = true;
    }

    public Customer(String id) {
        super(id);
        this.active = true;
    }

    // Getters and Setters with validation
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
        this.email = email != null ? email.trim() : null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("[0-9+\\-\\s]{8,20}")) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
        }
        this.phone = phone != null ? phone.trim() : null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Archives (soft deletes) the customer instead of permanent deletion
     */
    public void archive() {
        this.active = false;
        this.markAsUpdated();
    }

    /**
     * Reactivates an archived customer
     */
    public void reactivate() {
        this.active = true;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + getId() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", active=" + active +
                '}';
    }
}
