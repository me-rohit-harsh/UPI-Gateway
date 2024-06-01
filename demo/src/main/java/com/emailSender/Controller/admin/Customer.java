package com.emailSender.Controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Customer {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/customerlist")
    public String showDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
        if (isAuthenticated != null && isAuthenticated) {
            // Get authenticated user ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                // Find user by ID
                User user = userRepository.findById(userId).orElse(null);

                model.addAttribute("user", user);
                model.addAttribute("users", userRepository.findAll());
                return "/admin/customerList";

            }
            redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
            return "redirect:/admin/signin";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
        return "redirect:/admin/signin";

    }
}
