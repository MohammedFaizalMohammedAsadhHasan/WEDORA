package com.wedora.model;

import java.time.LocalDate;

/**
 * Wedding event entity.
 * Demonstrates OOP concept: Encapsulation
 * Private fields with controlled access through getters/setters.
 * Inherits common behavior from BaseEntity.
 */
public class Wedding extends BaseEntity {
    private String customerId;
    private String brideName;
    private String groomName;
    private LocalDate weddingDate;
    private String venue;
    private Integer estimatedGuests;
    private WeddingStatus status;
    private String notes;

    public enum WeddingStatus {
        PLANNING,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public Wedding() {
        super();
        this.status = WeddingStatus.PLANNING;
    }

    public Wedding(String id) {
        super(id);
        this.status = WeddingStatus.PLANNING;
    }

    // Getters and Setters with validation
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        this.customerId = customerId.trim();
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        if (brideName == null || brideName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bride name cannot be empty");
        }
        this.brideName = brideName.trim();
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        if (groomName == null || groomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Groom name cannot be empty");
        }
        this.groomName = groomName.trim();
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(LocalDate weddingDate) {
        if (weddingDate == null) {
            throw new IllegalArgumentException("Wedding date cannot be null");
        }
        if (weddingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Wedding date cannot be in the past");
        }
        this.weddingDate = weddingDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue != null ? venue.trim() : null;
    }

    public Integer getEstimatedGuests() {
        return estimatedGuests;
    }

    public void setEstimatedGuests(Integer estimatedGuests) {
        if (estimatedGuests != null && estimatedGuests < 0) {
            throw new IllegalArgumentException("Estimated guests cannot be negative");
        }
        this.estimatedGuests = estimatedGuests;
    }

    public WeddingStatus getStatus() {
        return status;
    }

    public void setStatus(WeddingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : null;
    }

    /**
     * Checks if wedding is upcoming (date is in future and not cancelled)
     */
    public boolean isUpcoming() {
        return weddingDate.isAfter(LocalDate.now()) && status != WeddingStatus.CANCELLED;
    }

    /**
     * Cancels the wedding
     */
    public void cancel() {
        this.status = WeddingStatus.CANCELLED;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "Wedding{" +
                "id='" + getId() + '\'' +
                ", brideName='" + brideName + '\'' +
                ", groomName='" + groomName + '\'' +
                ", weddingDate=" + weddingDate +
                ", status=" + status +
                '}';
    }
}
