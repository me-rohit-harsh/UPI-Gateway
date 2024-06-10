package com.emailSender.Controller;

import java.io.File;
import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.ContactRepository;
import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.Service.ImageStorageService;
import com.emailSender.model.Contact;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping("/save-utr")
    public String saveTxGetResponse(@ModelAttribute Transaction transaction, HttpSession session,
            @RequestParam(name = "paymentSS", required = false) MultipartFile screenshot,
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
            if (isUtrAlreadyProcessed(submittedUtr, user, redirectAttributes)) {
                return "redirect:/dashboard";
            }
            if (transaction.getMethod().equals("USDT (TRC20)") || transaction.getMethod().equals("USDT (BEP20)")
                    || transaction.getMethod().equals("USDT (BNB)")) {

                return processUsdtTransaction(transaction, user, redirectAttributes, screenshot);
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

    public String uploadImage(MultipartFile file) {

        String fileName = "";
        try {
            fileName = imageStorageService.storeFile(file);
            System.out.println("Image uploaded successfully. File name: " + fileName);
        } catch (IOException e) {
            System.out.println("Image uploaded failed. File name: " + fileName);
            e.printStackTrace();
        }
        return "/images/usdt/" + fileName;

    }

    @Autowired
    private JavaMailSender emailSender;

    @Async
    public void sendEmailWithAttachment(String to, String subject, String text, MultipartFile attachment)
            throws MessagingException, IOException {

        MimeMessage message = emailSender.createMimeMessage();

        // Use the true flag to indicate a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // Add the attachment
        helper.addAttachment(attachment.getOriginalFilename(), attachment);

        emailSender.send(message);
        System.out.println("Email Sent with attachment: " + attachment.getOriginalFilename());
    }

    private String processUsdtTransaction(Transaction transaction, User user, RedirectAttributes redirectAttributes,
            MultipartFile screenshot) {
        Transaction tx = new Transaction();
        tx.setAmount(transaction.getAmount());
        tx.setMethod(transaction.getMethod());
        tx.setRefId(transaction.getRefId());
        tx.setUser(user);
        tx.setType("Credit");
        tx.setStatus("Pending");
        tx.setScreenshot(uploadImage(screenshot));

        try {
            // Calculate the USDT value
            double usdtAmount = transaction.getAmount() / 87.0; // Assuming the rate is 87
            String usdtAmountFormatted = String.format("%.3f", usdtAmount);

            sendEmailWithAttachment("jixwallet@gmail.com", "USDT Transaction Alert",
                    "Transaction Details:\n\n" +
                            "User name: " + user.getUsername() + "\n" +
                            // "Transaction ID: " + transaction.getId() + "\n" +
                            "USDT Amount: " + usdtAmountFormatted + "\n" +
                            "Amount: " + transaction.getAmount() + "\n" +
                            "Method: " + transaction.getMethod() + "\n" +
                            "Ref ID: " + transaction.getRefId() + "\n" +
                            // "Date & Time: " + transaction.getCreatedAt() + "\n" +
                            "Type: Credit\n" +
                            "Status: Pending\n" +
                            "Screenshot: Attached",
                    screenshot);
        } catch (MessagingException | IOException e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Your payment has encountred some issue due to some internal proeblm please try again");
            e.printStackTrace();
            return "redirect:/dashboard";
        }
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

        if (utrFound) {
            if (user.getEmail() != null) {
                emailService.sendSimpleEmail(user.getEmail(), "Payment Confirmation - Welcome to Jixwallet",
                        "Welcome to Jixwallet. Your payment of Rs" + transaction.getAmount()
                                + " has been successfully received.");
            }
            user.setBalance(user.getBalance() + transaction.getAmount());
            userRepository.save(user);
            tx.setStatus("Successful");
            transactionRepository.save(tx);
            redirectAttributes.addFlashAttribute("message", "Payment has been added to your wallet successfully");
        } else {
            if (user.getEmail() != null) {
                emailService.sendSimpleEmail(user.getEmail(), "Payment Rejection - Jixwallet",
                        "Welcome to Jixwallet. Your payment of Rs" + transaction.getAmount() + " has failed.");
            }
            tx.setStatus("Failed");
            transactionRepository.save(tx);
            redirectAttributes.addFlashAttribute("errorMsg", "Payment has failed!");
        }

        return "redirect:/dashboard";
    }

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/contact")
    public String submitContactForm(@Valid @ModelAttribute Contact contactForm,
            BindingResult result, HttpSession session, Model model,
            @RequestParam("attachment") MultipartFile file, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "contact";
        }

        if (!file.isEmpty()) {
            contactForm.setAttachmentPath(uploadImage(file));
            try {
                String emailBody = "Subject: " + contactForm.getSubject() + "\n" +
                        "Name: " + contactForm.getName() + "\n" +
                        "Email: " + contactForm.getEmail() + "\n" +
                        "Message: " + contactForm.getMessage() + "\n";

                sendEmailWithAttachment("jixwallet@gmail.com", "New Contact Form Submmision - Jixwallet",
                        emailBody, file);
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMsg",
                        "Somethingh went worng please try again!");
                return "redirect:/help-center";
            }
        }

        contactRepository.save(contactForm);
        redirectAttributes.addFlashAttribute("message",
                "Your contact form has been submited please check your mail for further updates");

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            // Find user by ID
            User user = userRepository.findById(userId).orElse(null);
            if (user.getEmail() != null) {
                String body = "Dear Customer,\n\n" +
                        "Thank you for contacting us. Your inquiry ID is: " + contactForm.getId() + "\n\n" +
                        "We have received your inquiry and will get back to you as soon as possible.\n\n" +
                        "Best regards,\n" +
                        "Jixwallet Team";
                emailService.sendSimpleEmail(user.getEmail(), "Inquiry  Submission Update - Jixwallet", body);
            }

        }
        return "redirect:/help-center";
    }

}
