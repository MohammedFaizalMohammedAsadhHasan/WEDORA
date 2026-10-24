package com.wedora.controller;

import com.wedora.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * Controller for authentication including OTP verification.
 */
@Controller
public class AuthController {

    private final OtpService otpService;

    @Autowired
    public AuthController(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Displays OTP verification page.
     */
    @GetMapping("/verify-otp")
    public String showOtpPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("pendingEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login";
        }

        model.addAttribute("email", email);
        model.addAttribute("expiryMinutes", otpService.getOtpExpiryMinutes());
        return "auth/verify-otp";
    }

    /**
     * Processes OTP verification.
     */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            boolean isValid = otpService.validateOtp(email, otp);

            if (isValid) {
                session.removeAttribute("pendingEmail");
                session.setAttribute("authenticated", true);
                session.setAttribute("userEmail", email);
                redirectAttributes.addFlashAttribute("success", "OTP verified successfully!");
                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid or expired OTP. Please try again.");
                return "redirect:/verify-otp";
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "OTP verification failed. Please try again.");
            return "redirect:/verify-otp";
        }
    }

    /**
     * Resends OTP.
     */
    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("pendingEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login";
        }

        try {
            String newOtp = otpService.generateOtp(email);
            // In production, this would send an email/SMS
            // For development, we'll show it in the redirect message
            redirectAttributes.addFlashAttribute("success", "New OTP sent: " + newOtp + " (Valid for " + otpService.getOtpExpiryMinutes() + " minutes)");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to resend OTP. Please try again.");
        }

        return "redirect:/verify-otp";
    }
}
