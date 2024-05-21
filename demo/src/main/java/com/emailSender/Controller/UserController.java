package com.emailSender.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/signin")
    public String showSignupForm() {
        return "signin";
    }

    @PostMapping("/signin")
    public String login(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
            Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        // Check if the loginId is an email or username
        User user = userRepository.findByUsernameOrEmail(loginId, loginId);
//        System.out.println(user);
        if (user == null || !user.getPassword().equals(password)) {   
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid username/email or password");
            return "redirect:/signin";
        }
        session.setAttribute("auth", true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("user", user);
        session.setAttribute("userEmail",user.getEmail());
        redirectAttributes.addFlashAttribute("message", "Logged In");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Auth: " + session.getAttribute("auth"));
        System.out.println("User ID: " + session.getAttribute("userId"));
//        System.out.println("Session User: " + session.getAttribute("user"));
//        System.out.println("user"+ user);
        return "redirect:/pay";
    }
    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") @Valid User user, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Something Went Wrong!");
            return "redirect:/signup";
        }

        // Check if the username is empty or consists only of whitespace characters
        if (user.getUsername().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Username is not valid!");
            return "redirect:/signup";
        }

        // Check if the username already exists in the database
        if (userRepository.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMsg", "Username is already taken");
            return "redirect:/signup";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMsg", "Email is alreday registred!");

            return "redirect:/signup";
        }

        user.setBalance(0.0); // Set initial balance to 0
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Account created successfully!");
        return "redirect:/signin";
    }

    @PostMapping("/api/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsernameAvailability(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        boolean available = !userRepository.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        return response;
    }

}
