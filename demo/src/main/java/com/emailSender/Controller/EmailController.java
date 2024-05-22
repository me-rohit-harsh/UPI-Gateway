package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.EmailRepository;
import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.model.Email;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Controller
public class EmailController {
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailRepository emailRepository;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/sendEmail")
	public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text,
			RedirectAttributes redirectAttributes, HttpSession session) {
		emailService.sendSimpleEmail(to, subject, text);
		Email newEmail = new Email();
		newEmail.setSentFrom("alerts@jixwallet.com");
		newEmail.setStatus(true);
		newEmail.setToAddress(to);
		newEmail.setText(text);
		newEmail.setSubject(subject);
		emailRepository.save(newEmail);
		redirectAttributes.addFlashAttribute("success", true);
		// } else {
		// redirectAttributes.addFlashAttribute("error", "User not found.");
		// }

		return "redirect:/mail";
	}

	@Autowired
	private JavaMailSender mailSender;

	@Async
	void sendOTP(String to, String subject, String text) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);
			Email newEmail = new Email();
			mailSender.send(message);
			newEmail.setSentFrom("alerts@jixwallet.com");
			newEmail.setStatus(true);
			newEmail.setToAddress(to);
			newEmail.setText(text);
			newEmail.setSubject(subject);
			emailRepository.save(newEmail);
			System.out.println("OTP Sent: " + newEmail);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/fetchEmail")
	public String fetchEmails(HttpSession session, RedirectAttributes redirectAttributes) {
		System.out.println("here is the flow");
		try {
			Long userId = (Long) session.getAttribute("userId");
			User user = userRepository.findById(userId).orElse(null);
			// Retrieve submittedUtr and moneySent from session attributes
			String submittedUtr = (String) session.getAttribute("submittedUtr");
			Double moneySent = (Double) session.getAttribute("moneySent");

			// Call fetchEmails from emailService and get the result
			boolean utrFound = emailService.fetchEmails(submittedUtr, moneySent);
			System.out.println("here is the flow111");
			System.err.println("User Email ID is:" + user.getEmail());
			// Set the status based on the result
			if (utrFound) {
				// UTR was found, set status accordingly
				Transaction newTransaction = (Transaction) session.getAttribute("newTransaction");
				newTransaction.setStatus(true);
				newTransaction.setType("Credited");
				transactionRepository.save(newTransaction);
				// Call the sendEmail method asynchronously
				String userEmail = user.getEmail();
				if (userEmail != null) {
					emailService.sendSimpleEmail(userEmail, "Payment Confirmation - Welcome to Jixwallet",
							"Welcome to the Jixwallet. Your payment of Rs" + moneySent
									+ " has been successfully received.");
				}
				redirectAttributes.addFlashAttribute("message", "Payment has been added to your wallet successfully");
				return "redirect:/dashboard";
			} else {
				System.out.println("here is the flow22 Email controller");

				// UTR was not found, redirect to error page
				String userEmail = user.getEmail();
				if (userEmail != null) {
					emailService.sendSimpleEmail(userEmail, "Payment Rejection - Jixwallet",
							"Welcome to the Jixwallet. Your payment of Rs" + moneySent + " has failed  .");
				}
				redirectAttributes.addFlashAttribute("errorMsg", "Payment has been failed!");

				// redirectAttributes.addFlashAttribute("error", true);
				return "redirect:/dashboard";
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
