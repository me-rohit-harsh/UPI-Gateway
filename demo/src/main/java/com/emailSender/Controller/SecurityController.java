package com.emailSender.Controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
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
    @Autowired
    private EmailService emailService;

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

        if (request.getParameter("oldSecCode").equals(user.getSecCode())) {
            if (request.getParameter("newSecCode").equals(request.getParameter("newSecCodeCopy"))
                    && request.getParameter("newSecCode").length() == 6) {
                user.setSecCode(request.getParameter("newSecCode"));
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("message", "Your Security code has been changed successfully!");
                return "redirect:/security";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "You have entred incorrent Security Code");
            return "redirect:/security";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Oops Somethingh went wrong!");
        return "redirect:/security";

    }

    @PostMapping("/forgot-password")
    public String forgetPassword(@RequestParam("forgotEmailOrUsername") String EmailOrUsername,
            RedirectAttributes redirectAttributes) {
        System.out.println("in forget password postmapping");
        User user = userRepository.findByUsernameOrEmail(EmailOrUsername, EmailOrUsername);

        if (user != null) {
            // Generate a random UUID and remove the hyphens
            String uuid = UUID.randomUUID().toString().replace("-", "");
            user.setPassword(uuid.substring(0, 8));
            userRepository.save(user);
            String bodyMsg = String.format(
                    "Dear %s,\n\n" +
                            "You recently requested to reset your password for your account. \n\n" +
                            "Your temporary password is: %s\n\n" +
                            "Please log in to your account using this temporary password and then visit the security settings page to set a new password of your choice.\n\n"
                            +
                            "If you did not request a password reset, please ignore this email. \n\n" +
                            "Thank you!\n\n" +
                            "Your Company Name",
                    user.getUsername(), user.getPassword());
            emailService.sendSimpleEmail(user.getEmail(), "Password Reset Request - JixWallet", bodyMsg);
            redirectAttributes.addFlashAttribute("message", "Password has been sent to your registered email.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "This email is not registered with us!");
        }

        return "redirect:/signin";
    }
}