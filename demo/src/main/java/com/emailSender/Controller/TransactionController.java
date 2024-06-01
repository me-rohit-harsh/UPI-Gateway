package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save-utr")
    public String saveTxGetResponse(@ModelAttribute Transaction transaction, HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {

                redirectAttributes.addFlashAttribute("errorMsg", "User not logged in");
                return "redirect:/signin";
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "User not found");
                return "redirect:/signin";
            }

            String submittedUtr = transaction.getRefId();
            System.out.println("*****************");
            System.out.println(transaction.getMethod());
            System.out.println("*****************");
            // Double moneySent = transaction.getAmount();
            // String method = transaction.getMethod();

            if (isUtrAlreadyProcessed(submittedUtr, user, redirectAttributes)) {
                return "redirect:/dashboard";
            }
            if (transaction.getMethod().equals("USDT (TRC20)") || transaction.getMethod().equals("USDT (BEP20)")
                    || transaction.getMethod().equals("USDT (BNB)")) {
                return processUsdtTransaction(transaction, user, redirectAttributes);
            }

            else {
                return processTransaction(transaction, user, redirectAttributes);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Payment has failed due to an internal error.");
            return "redirect:/dashboard";
        }
    }

    private boolean isUtrAlreadyProcessed(String submittedUtr, User user, RedirectAttributes redirectAttributes) {
        Transaction existingTransaction = transactionRepository.findByRefId(submittedUtr);
        if (existingTransaction != null) {
            if (user.getEmail() != null) {
                emailService.sendSimpleEmail(user.getEmail(), "Payment Rejection - Jixwallet",
                        "Your payment has already been processed for Transaction ID: " + submittedUtr);

            }
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Your payment has already been processed for Tx ID: " + submittedUtr);
            return true;
        }
        return false;
    }

    private String processUsdtTransaction(Transaction transaction, User user, RedirectAttributes redirectAttributes) {
        Transaction tx = new Transaction();
        tx.setAmount(transaction.getAmount());
        tx.setMethod(transaction.getMethod());
        tx.setRefId(transaction.getRefId());
        tx.setUser(user);
        tx.setType("Credit");
        tx.setStatus(false);
        transactionRepository.save(tx);
        redirectAttributes.addFlashAttribute("message",
                "Your payment is being processed. It may take up to 24 hours to complete. Thank you for your patience.");
        return "redirect:/dashboard";
    }

    private String processTransaction(Transaction transaction, User user, RedirectAttributes redirectAttributes)
            throws Exception {
        boolean utrFound = emailService.fetchEmails(transaction.getRefId(), transaction.getAmount());

        Transaction tx = new Transaction();
        tx.setAmount(transaction.getAmount());
        tx.setMethod(transaction.getMethod());
        tx.setRefId(transaction.getRefId());
        tx.setUser(user);
        tx.setType("Credit");
        tx.setStatus(utrFound);

        if (utrFound) {
            if (user.getEmail() != null) {
                emailService.sendSimpleEmail(user.getEmail(), "Payment Confirmation - Welcome to Jixwallet",
                        "Welcome to Jixwallet. Your payment of Rs" + transaction.getAmount()
                                + " has been successfully received.");
            }
            user.setBalance(user.getBalance() + transaction.getAmount());
            userRepository.save(user);
            transactionRepository.save(tx);
            redirectAttributes.addFlashAttribute("message", "Payment has been added to your wallet successfully");
        } else {
            if (user.getEmail() != null) {
                emailService.sendSimpleEmail(user.getEmail(), "Payment Rejection - Jixwallet",
                        "Welcome to Jixwallet. Your payment of Rs" + transaction.getAmount() + " has failed.");
            }
            transactionRepository.save(tx);
            redirectAttributes.addFlashAttribute("errorMsg", "Payment has failed!");
        }

        return "redirect:/dashboard";
    }

}
