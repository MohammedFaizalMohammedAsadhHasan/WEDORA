package com.wedora.repository;

import com.wedora.model.Booking;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Booking persistence using TXT files.
 */
@Repository
public class BookingRepository {

    private static final String BOOKINGS_FILE = "bookings.txt";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Finds all bookings.
     */
    public List<Booking> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(BOOKINGS_FILE);
        List<Booking> bookings = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Booking booking = fromString(line);
                    bookings.add(booking);
                } catch (Exception e) {
                    System.err.println("Error parsing booking line: " + line);
                }
            }
        }

        return bookings;
    }

    /**
     * Finds booking by ID.
     */
    public Optional<Booking> findById(String id) throws IOException {
        List<Booking> bookings = findAll();
        return bookings.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds bookings by wedding ID.
     */
    public List<Booking> findByWeddingId(String weddingId) throws IOException {
        return findAll().stream()
                .filter(b -> b.getWeddingId() != null && b.getWeddingId().equals(weddingId))
                .collect(Collectors.toList());
    }

    /**
     * Finds bookings by vendor ID.
     */
    public List<Booking> findByVendorId(String vendorId) throws IOException {
        return findAll().stream()
                .filter(b -> b.getVendorId() != null && b.getVendorId().equals(vendorId))
                .collect(Collectors.toList());
    }

    /**
     * Finds confirmed bookings for a vendor within a date range.
     * Used for conflict detection.
     */
    public List<Booking> findConfirmedByVendorAndDateTimeRange(
            String vendorId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end) throws IOException {
        return findAll().stream()
                .filter(b -> b.getVendorId().equals(vendorId))
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .filter(b -> !b.getBookingDateTime().isAfter(end) && !b.getBookingDateTime().isBefore(start))
                .collect(Collectors.toList());
    }

    /**
     * Finds bookings by status.
     */
    public List<Booking> findByStatus(Booking.BookingStatus status) throws IOException {
        return findAll().stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Saves a booking (create or update).
     */
    public void save(Booking booking) throws IOException {
        List<Booking> bookings = findAll();

        boolean exists = bookings.stream()
                .anyMatch(b -> b.getId().equals(booking.getId()));

        if (exists) {
            bookings = bookings.stream()
                    .map(b -> b.getId().equals(booking.getId()) ? booking : b)
                    .collect(Collectors.toList());
        } else {
            bookings.add(booking);
        }

        booking.markAsUpdated();
        writeAll(bookings);
    }

    /**
     * Deletes a booking by ID.
     */
    public void delete(String id) throws IOException {
        List<Booking> bookings = findAll();
        bookings = bookings.stream()
                .filter(b -> !b.getId().equals(id))
                .collect(Collectors.toList());
        writeAll(bookings);
    }

    /**
     * Counts total bookings.
     */
    public long count() throws IOException {
        return findAll().size();
    }

    /**
     * Counts active bookings (not cancelled).
     */
    public long countActive() throws IOException {
        return findAll().stream()
                .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
                .count();
    }

    /**
     * Writes all bookings to file.
     */
    private void writeAll(List<Booking> bookings) throws IOException {
        List<String> lines = bookings.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(BOOKINGS_FILE, lines);
    }

    /**
     * Converts Booking to string for file storage.
     * Format: id|weddingId|vendorId|bookingDateTime|packageName|price|status|notes|createdAt|updatedAt
     */
    private String toString(Booking booking) {
        return String.join("|",
                booking.getId(),
                booking.getWeddingId(),
                booking.getVendorId(),
                booking.getBookingDateTime().format(DATETIME_FORMATTER),
                escape(booking.getPackageName()),
                booking.getPrice() != null ? booking.getPrice().toString() : "",
                booking.getStatus().name(),
                escape(booking.getNotes()),
                booking.getCreatedAt().format(DATETIME_FORMATTER),
                booking.getUpdatedAt().format(DATETIME_FORMATTER)
        );
    }

    /**
     * Converts string from file to Booking.
     */
    private Booking fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid booking data format");
        }

        Booking booking = new Booking(parts[0]);
        booking.setWeddingId(parts[1]);
        booking.setVendorId(parts[2]);
        booking.setBookingDateTime(java.time.LocalDateTime.parse(parts[3], DATETIME_FORMATTER));
        booking.setPackageName(unescape(parts[4]));
        booking.setPrice(parts[5].isEmpty() ? null : new BigDecimal(parts[5]));
        booking.setStatus(Booking.BookingStatus.valueOf(parts[6]));
        booking.setNotes(unescape(parts[7]));
        booking.setCreatedAt(java.time.LocalDateTime.parse(parts[8], DATETIME_FORMATTER));
        booking.setUpdatedAt(java.time.LocalDateTime.parse(parts[9], DATETIME_FORMATTER));

        return booking;
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
