package com.emailSender.Controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.ContactRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.model.Contact;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

/**
 * SupportController
 */
@Controller
@RequestMapping("admin")
public class SupportController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    
    @GetMapping("/support")
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
                    List<Contact> contact = contactRepository.findAll();
                    model.addAttribute("contactForms", contact);

                    return "admin/support";
                }

            }
            redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
            return "redirect:/admin/signin";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
        return "redirect:/admin/signin";
    }

}