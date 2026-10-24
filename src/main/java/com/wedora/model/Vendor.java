package com.wedora.model;

/**
 * Vendor entity representing a service provider.
 * Demonstrates OOP concept: Encapsulation
 * Private fields with controlled access through getters/setters.
 * Inherits common behavior from BaseEntity.
 */
public class Vendor extends BaseEntity {
    private String name;
    private String category;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String serviceDescription;
    private String pricing;
    private boolean active;

    public Vendor() {
        super();
        this.active = true;
    }

    public Vendor(String id) {
        super(id);
        this.active = true;
    }

    // Getters and Setters with validation
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.category = category.trim();
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson != null ? contactPerson.trim() : null;
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

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription != null ? serviceDescription.trim() : null;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing != null ? pricing.trim() : null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Archives (soft deletes) the vendor instead of permanent deletion
     */
    public void archive() {
        this.active = false;
        this.markAsUpdated();
    }

    /**
     * Reactivates an archived vendor
     */
    public void reactivate() {
        this.active = true;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }
}
