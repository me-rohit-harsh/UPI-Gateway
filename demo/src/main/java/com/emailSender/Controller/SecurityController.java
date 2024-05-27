package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;

import com.emailSender.model.User;

import jakarta.servlet.http.HttpServletRequest;
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

         return "redirect:/signin";
    }

    @PostMapping("changePassword")
    public String changePassword(HttpServletRequest request, HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById((Long) session.getAttribute("userId")).orElse(null);

        if (request.getParameter("oldPassword").equals(user.getPassword())
                && request.getParameter("newPassword").equals(request.getParameter("newPasswordCopy"))) {
            user.setPassword(request.getParameter("newPassword"));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Your Password has been updated successfully!");
            return "redirect:/security";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Something Went Wrong!");
        return "redirect:/security";
    }

    @PostMapping("changesecuritycode")
    public String changeSecurityCode(HttpServletRequest request, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findById((Long) session.getAttribute("userId")).orElse(null);

        if (request.getParameter("oldSecCode").equals(user.getSecCode())
                && request.getParameter("newSecCode").equals(request.getParameter("newSecCodeCopy"))) {
            user.setSecCode(request.getParameter("newSecCode"));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Your Security code has been changed successfully!");
            return "redirect:/security";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong");

        return "redirect:/security";

    }
}