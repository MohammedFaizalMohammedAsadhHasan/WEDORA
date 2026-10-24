
package com.wedora.controller;

import com.wedora.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class PageController {

    private final OtpService otpService;
    private final CustomerService customerService;
    private final VendorService vendorService;
    private final WeddingService weddingService;
    private final BookingService bookingService;
    private final TaskService taskService;
    private final FinanceService financeService;

    @Autowired
    public PageController(OtpService otpService, CustomerService customerService, VendorService vendorService,
                          WeddingService weddingService, BookingService bookingService,
                          TaskService taskService, FinanceService financeService) {
        this.otpService = otpService;
        this.customerService = customerService;
        this.vendorService = vendorService;
        this.weddingService = weddingService;
        this.bookingService = bookingService;
        this.taskService = taskService;
        this.financeService = financeService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) throws IOException {

        // Accept any email and password - auto-create session
        if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
            session.setAttribute("authenticated", true);
            session.setAttribute("userEmail", username);
            redirectAttributes.addFlashAttribute("success", "Welcome back, " + username + "!");
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("error", "Please enter email and password");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws IOException {
        // Fetch real statistics
        long totalCustomers = customerService.getCustomerCount();
        long activeCustomers = customerService.getActiveCustomerCount();
        long totalVendors = vendorService.getVendorCount();
        long activeVendors = vendorService.getActiveVendorCount();
        long totalWeddings = weddingService.getWeddingCount();
        long upcomingWeddings = weddingService.getUpcomingWeddingCount();
        long totalBookings = bookingService.getBookingCount();
        long activeBookings = bookingService.getActiveBookingCount();
        long overdueTasks = taskService.getOverdueTasks().size();

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("activeCustomers", activeCustomers);
        model.addAttribute("totalVendors", totalVendors);
        model.addAttribute("activeVendors", activeVendors);
        model.addAttribute("totalWeddings", totalWeddings);
        model.addAttribute("upcomingWeddings", upcomingWeddings);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("overdueTasks", overdueTasks);

        // Fetch recent activities
        model.addAttribute("recentWeddings", weddingService.getUpcomingWeddings().subList(0, Math.min(5, weddingService.getUpcomingWeddings().size())));
        model.addAttribute("recentTasks", taskService.getOverdueTasks().subList(0, Math.min(5, taskService.getOverdueTasks().size())));

        return "dashboard";
    }
}
