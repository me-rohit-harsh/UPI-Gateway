package com.emailSender.Controller.admin;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Controller.UserController;
import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class Admin {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signin")
    public String showLoginForm() {
        return "admin/login";
    }

    @PostMapping("adminlogin")
    public String adminLogin(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
            RedirectAttributes redirectAttributes, HttpSession session) {

        // Check if the loginId is an email or username
        User user = userRepository.findByUsernameOrEmail(loginId, loginId);
        if (user == null || !user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid username/email or password! Please try again");
            return "redirect:/admin/signin";
        }
        if (user.getRole().equals("Admin")) {
            // System.out.println(user);

            session.setAttribute("auth", true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("user", user);
            session.setAttribute("userEmail", user.getEmail());

            System.out.println("Session ID: " + session.getId());
            // System.out.println("Auth: " + session.getAttribute("auth"));
            System.out.println("User ID: " + session.getAttribute("userId"));
            // System.out.println("Session User: " + session.getAttribute("user"));
            // System.out.println("user"+ user);
            user.setLastLogin(new Date());
            userRepository.save(user);

            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "You are not authorized to access!");
            return "redirect:/signin";
        }
    }

    @GetMapping("/signout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalidate the session to remove all session attributes
        session.invalidate();
        // Add a flash attribute to notify the user they have been logged out
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        // Redirect to the login page or home page
        return "redirect:/admin/signin";
    }

    @Autowired
    private UserController userController;

    @GetMapping("/aauth/login-as-user/{userId}")
    public String loginAsUser(@PathVariable Long userId, HttpServletRequest request,
            RedirectAttributes redirectAttributes, HttpSession session) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
        if (isAuthenticated != null && isAuthenticated) {
            // Get authenticated user ID
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId != null) {
                // Find user by ID
                User admin = userRepository.findById(adminId).orElse(null);
                if (admin != null && admin.getRole().equals("Admin")) {
                    User user = userRepository.findById(userId).orElse(null);
                    userController.userAuth(user.getUsername(), user.getPassword(), redirectAttributes, session);
                    redirectAttributes.addFlashAttribute("message", "Logged in as user");
                    return "redirect:/dashboard"; // Redirect to user dashboard or any desired page
                }
            }
        }
        return "redirect:/admin/signin"; // Redirect to user dashboard or any desired page
    }
}
