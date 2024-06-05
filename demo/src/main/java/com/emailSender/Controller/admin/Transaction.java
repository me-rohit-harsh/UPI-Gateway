package com.emailSender.Controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("admin")
public class Transaction {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/transactionhistory")
    public String showTransactions(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
        if (isAuthenticated != null && isAuthenticated) {
            // Get authenticated user ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                // Find user by ID
                User user = userRepository.findById(userId).orElse(null);
                if (user.getRole().equals("Admin")) {

                    model.addAttribute("user", user);
                    model.addAttribute("transactions", transactionRepository.findAll());
                    return "admin/transactionHistory";

                }
            }
            redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
            return "redirect:/admin/signin";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
        return "redirect:/admin/signin";
    }

}

