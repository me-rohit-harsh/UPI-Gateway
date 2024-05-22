package com.emailSender.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.emailSender.Repository.UserRepository;

import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
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
              
                    return "client/profile"; 
                }
            }
        }

        // Redirect to sign-in page if not authenticated or user not found
        return "client/signin";
    }

    @PostMapping("/saveProfile")
    public String updateProfile(@RequestParam("name") String name,
            @RequestParam("address") String address, HttpSession session, RedirectAttributes redirectAttributes) {
        // Step 1: Retrieve the user object from the session
        User user = userRepository.findById((Long) session.getAttribute("userId")).orElse(null);
        if (user != null) {

            user.setAddress(address);
            user.setName(name);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Your Profile have been updated successfully!");
            return "redirect:/profile";
        } else {
            // Handle case where user is not found in session
            redirectAttributes.addFlashAttribute("errorMsg", "User session not found");
            return "redirect:/profile";
        }
    }

}
