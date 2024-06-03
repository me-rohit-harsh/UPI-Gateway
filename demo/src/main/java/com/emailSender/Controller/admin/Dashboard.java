package com.emailSender.Controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Controller.UserController;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.TransactionService;
import com.emailSender.Service.UserService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController
 */
@Controller
@RequestMapping("/admin")
public class Dashboard {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
        if (isAuthenticated != null && isAuthenticated) {
            // Get authenticated user ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                // Find user by ID
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                   
                    model.addAttribute("sumOfUsdt", transactionService.getSumOfUsdtTransactions());
                    model.addAttribute("user", user);
                    model.addAttribute("totalAmount", transactionService.calculateTotalAmount());
                    model.addAttribute("avaBalnce", userService.getTotalBalance());
                    redirectAttributes.addFlashAttribute("message", "Access Appreoved");
                    return "admin/dashboard";

                }
            }
            redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
            return "redirect:/admin/signin";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
        return "redirect:/admin/signin";

    }
}