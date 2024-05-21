package com.emailSender.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.TransactionService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

/**
 * SecurityController
 */
@Controller
public class SecurityController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/security")
    public String showSecForm(HttpSession session, Model model) {

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
                   
                    return "client/security"; 
                }
            }
        }

        return "client/signin";
    }

}