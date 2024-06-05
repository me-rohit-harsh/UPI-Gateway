package com.emailSender.Controller.admin;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public String showDashboard(@RequestParam(value = "value", required = false) String value,
                            HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
    if (isAuthenticated != null && isAuthenticated) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
        if(user.getRole().equals("Admin")){
            List<User> users;

            if (value != null && !value.isEmpty()) {
                try {
                    // Check if the value is an ID
                    Long id = Long.parseLong(value);
                    users = userRepository.findById(id).map(Collections::singletonList).orElse(Collections.emptyList());
                } catch (NumberFormatException e) {
                    // If not an ID, check for email or username
                    users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(value, value);
                }
            } else {
                users = userRepository.findAll();
            }

            model.addAttribute("user", user);
            model.addAttribute("users", users);
            return "admin/customerList";
        }
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested URL!");
        return "redirect:/admin/signin";
    }
    redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested URL!");
    return "redirect:/admin/signin";
}

}
