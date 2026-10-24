package com.wedora.service;

import com.wedora.dto.BookingDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Booking;
import com.wedora.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Booking business logic.
 * Includes conflict validation for vendor bookings.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Gets all bookings.
     */
    public List<BookingDTO> getAllBookings() throws IOException {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets booking by ID.
     */
    public BookingDTO getBookingById(String id) throws IOException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return toDTO(booking);
    }

    /**
     * Gets bookings by wedding ID.
     */
    public List<BookingDTO> getBookingsByWeddingId(String weddingId) throws IOException {
        List<Booking> bookings = bookingRepository.findByWeddingId(weddingId);
        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets bookings by vendor ID.
     */
    public List<BookingDTO> getBookingsByVendorId(String vendorId) throws IOException {
        List<Booking> bookings = bookingRepository.findByVendorId(vendorId);
        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new booking with conflict validation.
     */
    public BookingDTO createBooking(BookingDTO bookingDTO) throws IOException {
        Booking booking = toEntity(bookingDTO);

        // Check for vendor conflicts (same vendor, same time, confirmed status)
        LocalDateTime bookingTime = booking.getBookingDateTime();
        LocalDateTime start = bookingTime.minusHours(2); // 2-hour window before
        LocalDateTime end = bookingTime.plusHours(2); // 2-hour window after

        List<Booking> conflicts = bookingRepository.findConfirmedByVendorAndDateTimeRange(
                booking.getVendorId(), start, end);

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException(
                    "Vendor has a confirmed booking during this time. Please choose a different time or vendor.");
        }

        bookingRepository.save(booking);
        return toDTO(booking);
    }

    /**
     * Updates an existing booking.
     */
    public BookingDTO updateBooking(String id, BookingDTO bookingDTO) throws IOException {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Check for conflicts if time or vendor changed
        if (!existingBooking.getVendorId().equals(bookingDTO.getVendorId()) ||
            !existingBooking.getBookingDateTime().equals(bookingDTO.getBookingDateTime())) {

            LocalDateTime bookingTime = bookingDTO.getBookingDateTime();
            LocalDateTime start = bookingTime.minusHours(2);
            LocalDateTime end = bookingTime.plusHours(2);

            List<Booking> conflicts = bookingRepository.findConfirmedByVendorAndDateTimeRange(
                    bookingDTO.getVendorId(), start, end);

            // Exclude current booking from conflict check
            conflicts = conflicts.stream()
                    .filter(b -> !b.getId().equals(id))
                    .collect(Collectors.toList());

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException(
                        "Vendor has a confirmed booking during this time. Please choose a different time or vendor.");
            }
        }

        existingBooking.setWeddingId(bookingDTO.getWeddingId());
        existingBooking.setVendorId(bookingDTO.getVendorId());
        existingBooking.setBookingDateTime(bookingDTO.getBookingDateTime());
        existingBooking.setPackageName(bookingDTO.getPackageName());
        existingBooking.setPrice(bookingDTO.getPrice());
        existingBooking.setStatus(bookingDTO.getStatus());
        existingBooking.setNotes(bookingDTO.getNotes());

        bookingRepository.save(existingBooking);
        return toDTO(existingBooking);
    }

    /**
     * Confirms a booking.
     */
    public BookingDTO confirmBooking(String id) throws IOException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // Check for conflicts before confirming
        LocalDateTime bookingTime = booking.getBookingDateTime();
        LocalDateTime start = bookingTime.minusHours(2);
        LocalDateTime end = bookingTime.plusHours(2);

        List<Booking> conflicts = bookingRepository.findConfirmedByVendorAndDateTimeRange(
                booking.getVendorId(), start, end);

        conflicts = conflicts.stream()
                .filter(b -> !b.getId().equals(id))
                .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot confirm: Vendor has a confirmed booking during this time.");
        }

        booking.confirm();
        bookingRepository.save(booking);
        return toDTO(booking);
    }

    /**
     * Cancels a booking.
     */
    public void cancelBooking(String id) throws IOException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        booking.cancel();
        bookingRepository.save(booking);
    }

    /**
     * Deletes a booking.
     */
    public void deleteBooking(String id) throws IOException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        bookingRepository.delete(id);
    }

    /**
     * Gets booking count.
     */
    public long getBookingCount() throws IOException {
        return bookingRepository.count();
    }

    /**
     * Gets active booking count.
     */
    public long getActiveBookingCount() throws IOException {
        return bookingRepository.countActive();
    }

    /**
     * Converts DTO to Entity.
     */
    private Booking toEntity(BookingDTO dto) {
        Booking booking = new Booking(dto.getId());
        booking.setWeddingId(dto.getWeddingId());
        booking.setVendorId(dto.getVendorId());
        booking.setBookingDateTime(dto.getBookingDateTime());
        booking.setPackageName(dto.getPackageName());
        booking.setPrice(dto.getPrice());
        booking.setStatus(dto.getStatus() != null ? dto.getStatus() : Booking.BookingStatus.PENDING);
        booking.setNotes(dto.getNotes());
        return booking;
    }

    /**
     * Converts Entity to DTO.
     */
    private BookingDTO toDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getWeddingId(),
                booking.getVendorId(),
                booking.getBookingDateTime(),
                booking.getPackageName(),
                booking.getPrice(),
                booking.getStatus(),
                booking.getNotes()
        );
    }
}
