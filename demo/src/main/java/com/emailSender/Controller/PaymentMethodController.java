package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentMethodController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/method")
    public String showMethod(HttpSession session, Model model) {
        // Check if user is authenticated
        Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
        if (isAuthenticated != null && isAuthenticated) {
            // Get authenticated user ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                // Find user by ID
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    // System.out.println("User" + user);
                    model.addAttribute("user", user);

                    return "client/paymentmethod";
                }
            }
        }

        // Redirect to sign-in page if not authenticated or user not found
        return "client/signin";
    }
}
