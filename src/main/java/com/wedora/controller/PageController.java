
package com.wedora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp() {
        return "auth/verify-otp";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
