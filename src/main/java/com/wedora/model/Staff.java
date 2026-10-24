package com.wedora.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Staff entity for business employees.
 */
public class Staff extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private LocalDate hireDate;
    private String department;
    private BigDecimal salary;
    private boolean active;

    public Staff() {
        super();
        this.active = true;
    }

    public Staff(String id) {
        super(id);
        this.active = true;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName.trim();
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position != null ? position.trim() : null;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : null;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        if (salary != null && salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void archive() {
        this.active = false;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id='" + getId() + '\'' +
                ", name='" + getFullName() + '\'' +
                ", position='" + position + '\'' +
                ", active=" + active +
                '}';
    }
}
