package com.wedora.controller;

import com.wedora.dto.BookingDTO;
import com.wedora.service.BookingService;
import com.wedora.service.VendorService;
import com.wedora.service.WeddingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Booking management endpoints.
 */
@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final WeddingService weddingService;
    private final VendorService vendorService;

    @Autowired
    public BookingController(BookingService bookingService, WeddingService weddingService, VendorService vendorService) {
        this.bookingService = bookingService;
        this.weddingService = weddingService;
        this.vendorService = vendorService;
    }

    /**
     * Displays list of all bookings.
     */
    @GetMapping
    public String listBookings(Model model) throws IOException {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        model.addAttribute("totalBookings", bookingService.getBookingCount());
        model.addAttribute("activeBookings", bookingService.getActiveBookingCount());
        return "bookings/list";
    }

    /**
     * Displays form to create a new booking.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) throws IOException {
        model.addAttribute("booking", new BookingDTO());
        model.addAttribute("weddings", weddingService.getAllWeddings());
        model.addAttribute("vendors", vendorService.getActiveVendors());
        model.addAttribute("isEdit", false);
        return "bookings/form";
    }

    /**
     * Processes booking creation.
     */
    @PostMapping("/new")
    public String createBooking(
            @Valid @ModelAttribute("booking") BookingDTO bookingDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "bookings/form";
        }

        try {
            bookingService.createBooking(bookingDTO);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
            return "redirect:/bookings";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/new";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save booking. Please try again.");
            return "redirect:/bookings/new";
        }
    }

    /**
     * Displays form to edit an existing booking.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BookingDTO booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("weddings", weddingService.getAllWeddings());
            model.addAttribute("vendors", vendorService.getActiveVendors());
            model.addAttribute("isEdit", true);
            return "bookings/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings";
        }
    }

    /**
     * Processes booking update.
     */
    @PostMapping("/edit/{id}")
    public String updateBooking(
            @PathVariable String id,
            @Valid @ModelAttribute("booking") BookingDTO bookingDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "bookings/form";
        }

        try {
            bookingDTO.setId(id);
            bookingService.updateBooking(id, bookingDTO);
            redirectAttributes.addFlashAttribute("success", "Booking updated successfully!");
            return "redirect:/bookings";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/edit/" + id;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update booking. Please try again.");
            return "redirect:/bookings/edit/" + id;
        }
    }

    /**
     * Displays booking details.
     */
    @GetMapping("/{id}")
    public String viewBooking(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BookingDTO booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            return "bookings/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings";
        }
    }

    /**
     * Confirms a booking.
     */
    @PostMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.confirmBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking confirmed successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }

    /**
     * Cancels a booking.
     */
    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }

    /**
     * Deletes a booking.
     */
    @PostMapping("/delete/{id}")
    public String deleteBooking(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }
}
