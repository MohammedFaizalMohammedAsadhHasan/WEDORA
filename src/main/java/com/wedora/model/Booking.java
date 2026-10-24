package com.wedora.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Booking entity linking vendors to weddings.
 * Demonstrates OOP concept: Encapsulation
 * Private fields with controlled access through getters/setters.
 * Inherits common behavior from BaseEntity.
 */
public class Booking extends BaseEntity {
    private String weddingId;
    private String vendorId;
    private LocalDateTime bookingDateTime;
    private String packageName;
    private BigDecimal price;
    private BookingStatus status;
    private String notes;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }

    public Booking() {
        super();
        this.status = BookingStatus.PENDING;
    }

    public Booking(String id) {
        super(id);
        this.status = BookingStatus.PENDING;
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

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        if (vendorId == null || vendorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor ID cannot be empty");
        }
        this.vendorId = vendorId.trim();
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        if (bookingDateTime == null) {
            throw new IllegalArgumentException("Booking date/time cannot be null");
        }
        this.bookingDateTime = bookingDateTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName != null ? packageName.trim() : null;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
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
     * Confirms the booking
     */
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.markAsUpdated();
    }

    /**
     * Cancels the booking
     */
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        this.markAsUpdated();
    }

    /**
     * Marks booking as completed
     */
    public void complete() {
        this.status = BookingStatus.COMPLETED;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + getId() + '\'' +
                ", weddingId='" + weddingId + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", status=" + status +
                '}';
    }
}
