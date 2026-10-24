package com.wedora.dto;

import com.wedora.model.Wedding;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Data Transfer Object for Wedding operations.
 */
public class WeddingDTO {
    private String id;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Bride name is required")
    private String brideName;

    @NotBlank(message = "Groom name is required")
    private String groomName;

    @NotNull(message = "Wedding date is required")
    @Future(message = "Wedding date must be in the future")
    private LocalDate weddingDate;

    private String venue;

    @Min(value = 0, message = "Estimated guests cannot be negative")
    private Integer estimatedGuests;

    private Wedding.WeddingStatus status;

    private String notes;

    public WeddingDTO() {
        this.status = Wedding.WeddingStatus.PLANNING;
    }

    public WeddingDTO(String id, String customerId, String brideName, String groomName,
                     LocalDate weddingDate, String venue, Integer estimatedGuests,
                     Wedding.WeddingStatus status, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.brideName = brideName;
        this.groomName = groomName;
        this.weddingDate = weddingDate;
        this.venue = venue;
        this.estimatedGuests = estimatedGuests;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(LocalDate weddingDate) {
        this.weddingDate = weddingDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getEstimatedGuests() {
        return estimatedGuests;
    }

    public void setEstimatedGuests(Integer estimatedGuests) {
        this.estimatedGuests = estimatedGuests;
    }

    public Wedding.WeddingStatus getStatus() {
        return status;
    }

    public void setStatus(Wedding.WeddingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Finance fields (transient, not persisted)
    private java.math.BigDecimal totalExpenses;
    private java.math.BigDecimal totalBookings;
    private java.math.BigDecimal remainingBudget;

    public java.math.BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(java.math.BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public java.math.BigDecimal getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(java.math.BigDecimal totalBookings) {
        this.totalBookings = totalBookings;
    }

    public java.math.BigDecimal getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(java.math.BigDecimal remainingBudget) {
        this.remainingBudget = remainingBudget;
    }
}
