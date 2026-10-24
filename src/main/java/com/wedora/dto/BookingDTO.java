package com.wedora.dto;

import com.wedora.model.Booking;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Booking operations.
 */
public class BookingDTO {
    private String id;

    @NotBlank(message = "Wedding ID is required")
    private String weddingId;

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    @NotNull(message = "Booking date/time is required")
    private LocalDateTime bookingDateTime;

    private String packageName;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    private Booking.BookingStatus status;

    private String notes;

    public BookingDTO() {
        this.status = Booking.BookingStatus.PENDING;
    }

    public BookingDTO(String id, String weddingId, String vendorId, LocalDateTime bookingDateTime,
                      String packageName, BigDecimal price, Booking.BookingStatus status, String notes) {
        this.id = id;
        this.weddingId = weddingId;
        this.vendorId = vendorId;
        this.bookingDateTime = bookingDateTime;
        this.packageName = packageName;
        this.price = price;
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

    public String getWeddingId() {
        return weddingId;
    }

    public void setWeddingId(String weddingId) {
        this.weddingId = weddingId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Booking.BookingStatus getStatus() {
        return status;
    }

    public void setStatus(Booking.BookingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
