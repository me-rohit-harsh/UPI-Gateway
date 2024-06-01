package com.emailSender.Controller.admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.model.User;
import com.emailSender.model.Transaction;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("admin")

public class UsdtController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/usdt")
    public String usdt(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
        if (isAuthenticated != null && isAuthenticated) {
            // Get authenticated user ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                // Find user by ID
                User user = userRepository.findById(userId).orElse(null);
                // Get transactions with specified methods
                List<String> methods = Arrays.asList("USDT (BEP20)", "USDT (TRC20)", "USDT (BNB)");
                List<Transaction> transactions = transactionRepository.findByMethodIn(methods);

                model.addAttribute("user", user);
                model.addAttribute("transactions", transactions);
                return "admin/usdt";

            }
            redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
            return "redirect:/admin/signin";
        }
        redirectAttributes.addFlashAttribute("errorMsg", "Please Sign In to access the requested url!");
        return "redirect:/admin/signin";
    }

    @PostMapping("/transaction/update-status")
    public String updateTransactionStatus(@RequestParam Long transactionId, @RequestParam Boolean status,
            RedirectAttributes redirectAttributes, HttpSession session) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction != null) {
            transaction.setStatus(status);
            User user = transaction.getUser();
            Double preBalance = user.getBalance();
            String body;
            String subject;
            if (transaction.getStatus()) {
                user.setBalance(preBalance + transaction.getAmount());
                subject = "Transaction Approved";
                body = String.format(
                        "Dear %s,\n\nYour transaction with reference ID %s has been approved.\n\nAmount: %s\nNew Balance: %s\n\nThank you for using our service.",
                        user.getUsername(), transaction.getRefId(), transaction.getAmount(), user.getBalance());
            } else {
                user.setBalance(preBalance - transaction.getAmount());
                subject = "Transaction Declined";
                body = String.format(
                        "Dear %s,\n\nYour transaction with reference ID %s has been declined.\n\nAmount: %s\nNew Balance: %s\n\nIf you have any questions, please contact our support.",
                        user.getUsername(), transaction.getRefId(), transaction.getAmount(), user.getBalance());
            }
            transactionRepository.save(transaction);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("message", "Transaction status updated successfully.");

            emailService.sendSimpleEmail(user.getEmail(), subject, body);
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Transaction not found.");
        }

        return "redirect:/admin/usdt";
    }
}
