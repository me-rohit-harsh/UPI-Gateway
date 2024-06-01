package com.emailSender.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
import com.emailSender.Service.EmailService;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private EmailService emailServices;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/signin")
    public String showSignupForm() {
        return "login";
    }

    public Boolean userAuth(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
            RedirectAttributes redirectAttributes, HttpSession session) {
        // Check if the loginId is an email or username
        User user = userRepository.findByUsernameOrEmail(loginId, loginId);
        // System.out.println(user);
        if (user == null || !user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid username/email or password");
            return false;
        }
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
        return true;
    }

    @PostMapping("/signin")
    public String login(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
            Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (userAuth(loginId, password, redirectAttributes, session)) {
            redirectAttributes.addFlashAttribute("message",
                    "Accesss Approved!");
            return "redirect:/dashboard";
        }
        return "redirect:/signin";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") @Valid User user, BindingResult result,
            RedirectAttributes redirectAttributes, HttpSession session) {
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

        user.setBalance(0.0);
        user.setSecCode(generateSecretCode());
        user.setRole("Customer");
        userRepository.save(user);
        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "Welcome to JixWallet! We are thrilled to have you on board.\n\n" +
                        "Your account has been successfully created. Here are your account details:\n\n" +
                        "Username: %s\n" +
                        "Secret Code: %s\n\n" +
                        "Please keep this information safe and do not share it with anyone.\n\n" +
                        "If you have any questions or need further assistance, feel free to contact our support team.\n\n"
                        +
                        "Thank you for choosing JixWallet.\n\n" +
                        "Best Regards,\n" +
                        "The JixWallet Team",
                user.getUsername(), user.getUsername(), user.getSecCode());

        emailServices.sendSimpleEmail(user.getEmail(), "Account Registration Success - Jixwallet", emailBody);
        redirectAttributes.addFlashAttribute("message",
                "Check your email for registration confirmation and security code.");
        if (userAuth(user.getUsername(), user.getPassword(), redirectAttributes, session)) {
            redirectAttributes.addFlashAttribute("message",
                    "Check your email for registration confirmation and security code.");
            return "redirect:/dashboard";
        }
        return "redirect:/signin";
    }

    public static String generateSecretCode() {
        Random random = new Random();

        // Generate a random uppercase alphabet for the first character
        char firstChar = (char) (random.nextInt(26) + 'A');

        // Generate random digits for the rest of the characters
        StringBuilder restOfCode = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            restOfCode.append(random.nextInt(10));
        }

        // Combine the first character and the rest of the code
        return firstChar + restOfCode.toString();
    }

    @GetMapping("/signout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalidate the session to remove all session attributes
        session.invalidate();
        // Add a flash attribute to notify the user they have been logged out
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        // Redirect to the login page or home page
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
