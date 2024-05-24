package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.BankRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.BankService;
import com.emailSender.model.Bank;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class BankController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankService bankDetailsService;

    @GetMapping("/method")
    public String showMethod(HttpSession session, Model model) {
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
                    model.addAttribute("bankDetails", bankDetailsService.getLatestBankDetailsForCurrentUser(userId));
                    return "client/paymentmethod";
                }
            }
        }

        // Redirect to sign-in page if not authenticated or user not found
        return "signin";
    }

    @PostMapping("/saveBankDetails")
    public String saveBankDetails(@ModelAttribute Bank bank, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findById((Long) session.getAttribute("userId")).orElse(null);
        Bank newBank = new Bank();
        newBank.setBankName(bank.getBankName());
        newBank.setHolderName(bank.getHolderName());
        newBank.setAccountNo(bank.getAccountNo());
        newBank.setIfsc(bank.getIfsc());
        newBank.setBranch(bank.getBranch());
        newBank.setAcType(bank.getAcType());
        newBank.setUser(user);
        bankDetailsService.saveBankDetails(newBank);
        redirectAttributes.addFlashAttribute("message", "Payment has been added to your wallet successfully");
        return "redirect:/method";
    }
}
