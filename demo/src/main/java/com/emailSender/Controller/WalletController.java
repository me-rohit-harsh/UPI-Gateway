package com.emailSender.Controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.Service.WalletService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    EmailService emailService;

    @PostMapping("/process")
    public ResponseEntity<String> processTransaction(@RequestBody User request) {
        User user = userRepository.findByUsernameAndSecCode(request.getUsername(), request.getSecCode());
        boolean result = walletService.processTransaction(user, request.getBalance());
        if (result) {
            saveTransaction(request.getBalance(), user, true);
            return ResponseEntity.ok("Transaction successful");
        } else {
            saveTransaction(request.getBalance(), user, false);
            emailService.sendSimpleEmail(user.getEmail(), "Payment Rejection - Jixwallet",
                    "Welcome to Jixwallet. Your payment of Rs" + request.getBalance() + " has failed.");
            return ResponseEntity.status(400).body("Transaction failed");
        }
    }

    @Async
    private void saveTransaction(Double amount, User user, boolean status) {
        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setMethod("Oxycloud");
        tx.setRefId(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        tx.setUser(user);
        tx.setType("Debit");
        tx.setStatus(status);
        transactionRepository.save(tx);
        String emailBody = String.format(
                "We are pleased to inform you that your recent payment has been successfully processed.\n\n" +
                        "Amount: " + amount + "\n" +
                        "Date and Time:" + tx.getCreatedAt().toString() + "\n" +
                        "Payment Method: " + tx.getMethod())
                + "\n" +
                "Thank you for using our services!\n\n" +
                "Best regards,\n" +
                "Jixwallet";

        emailService.sendSimpleEmail(user.getEmail(), "Payment Confirmation - Your Payment was Successful", emailBody);

    }
}
