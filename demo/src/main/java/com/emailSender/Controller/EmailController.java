package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.EmailRepository;
import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.model.Email;
import com.emailSender.model.Transaction;

import jakarta.servlet.http.HttpSession;

@Controller
public class EmailController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailRepository emailRepository;

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text,
            RedirectAttributes redirectAttributes) {
        emailService.sendSimpleEmail(to, subject, text);
        Email newEmail = new Email();
        newEmail.setStatus(true);
        newEmail.setTo_address(to);
        newEmail.setText(text);
        newEmail.setSubject(subject);
        emailRepository.save(newEmail);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/mail";
    }

    @GetMapping("/fetchEmail")
    public String fetchEmails(HttpSession session, RedirectAttributes redirectAttributes) {
System.out.println("here is the flow");
        try {

            // Retrieve submittedUtr and moneySent from session attributes
            String submittedUtr = (String) session.getAttribute("submittedUtr");
            Integer moneySent = (Integer) session.getAttribute("moneySent");

            // Call fetchEmails from emailService and get the result
            boolean utrFound = emailService.fetchEmails(submittedUtr, moneySent);
            System.out.println("here is the flow111");

            // Set the status based on the result
            if (utrFound) {
                // UTR was found, set status accordingly
                Transaction newTransaction = (Transaction) session.getAttribute("newTransaction");
                newTransaction.setStatus(true);
                newTransaction.setType("Credited");
                transactionRepository.save(newTransaction);
                // Call the sendEmail method asynchronously
                String userEmail = (String) session.getAttribute("userEmail");
                if(userEmail!=null){
                emailService.sendSimpleEmail(userEmail, "Payment Confirmation - Welcome to Oxyclouds",
                        "Welcome to the Oxyclouds. Your payment of Rs" + moneySent
                                + " has been successfully received.");}
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/success";
            } else {
                System.out.println("here is the flow22 Email controller");

                // UTR was not found, redirect to error page
                String userEmail = (String) session.getAttribute("userEmail");
                if(userEmail!=null){
                emailService.sendSimpleEmail(userEmail, "Payment Rejection - Oxyclouds",
                        "Welcome to the Oxyclouds. Your payment of Rs" + moneySent
                                + " has failed  .");}
                session.setAttribute("SubmitAuthError", true);
                redirectAttributes.addFlashAttribute("error", true);
                return "redirect:/requesterror";
            }
            
        } catch (Exception e) {
            System.out.println("here is the flow3333");

            // Handle exceptions
            // e.printStackTrace(); // Print the stack trace for debugging
            System.out.println("Exception occurs in the email Controller" + e);
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/payment?error";
        }
    }

    @Async
    public void sendEmailAsync(String to, String subject, String body) {
        emailService.sendSimpleEmail(to, subject, body);
    }
}
