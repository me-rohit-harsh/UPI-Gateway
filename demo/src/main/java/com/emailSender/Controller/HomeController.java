package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.model.Transaction;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	private TransactionRepository transactionRepository;
@Autowired
private EmailService emailService;
	@GetMapping("/payment")
	public String showQR() {
		return ("payment");
	}

	@GetMapping("/mail")
	public String sendMail() {
		return ("mail");
	}

	@GetMapping("/success")
	public String success(HttpSession session) {
		Boolean SubmitAuth = (Boolean) session.getAttribute("SubmitAuth");
		if (SubmitAuth != null && SubmitAuth) {
			return "success";
		} else {
			return "redirect:/payment";
		}
	}

	@GetMapping("/requesterror")
	public String error(HttpSession session) {
		Boolean SubmitAuth = (Boolean) session.getAttribute("SubmitAuthError");
		if (SubmitAuth != null && SubmitAuth) {
			return "errorPage";
		} else {
			return "redirect:/payment";
		}
	}

	@PostMapping("/save-utr")
	public String saveUtr(@ModelAttribute Transaction transaction, HttpSession session) {
		session.setAttribute("SubmitAuth", true);
		String upiRefNo = transaction.getRefId();
		Integer amount = transaction.getAmmount();
		String method = transaction.getMethod();
		String email = transaction.getEmail();
		System.out.println("***********" +upiRefNo+' '+ amount+' '+method+' '+email+"***********");
		System.out.println("Here is the flow");
		try {
			// Check if the UTR already exists in the database
			Transaction existingTransaction = transactionRepository.findByRefId(upiRefNo);
			if (existingTransaction != null) {
				// UTR already exists, handle accordingly (e.g., show error message)
				// UTR was not found, redirect to error page
				String userEmail = (String) session.getAttribute("userEmail");
				if(userEmail!=null){
					emailService.sendSimpleEmail(userEmail, "Payment Rejection - Oxyclouds",
							"Your payment has already been processed.");
					System.out.println("Already Redeemed");
				}
				session.setAttribute("SubmitAuthError", true);

				// Redirect to an error page or return a response indicating UTR already exists
				return "redirect:/requesterror";
			}
			
			// Create a new Transaction object
			Transaction newTransaction = new Transaction();
			session.setAttribute("newTransaction", newTransaction);
			newTransaction.setRefId(upiRefNo);
			newTransaction.setStatus(false);
			newTransaction.setAmmount(amount);
			System.out.println("Here is the flow 11");
			newTransaction.setEmail(email);
			newTransaction.setMethod(method);
			// Save the new User object to the database
			transactionRepository.save(newTransaction);
			session.setAttribute("submittedUtr", newTransaction.getRefId());
			session.setAttribute("moneySent", newTransaction.getAmmount());
			session.setAttribute("userEmail", newTransaction.getEmail());
			// Redirect to the fetchEmail page to check for valid UTR

			System.out.println("Here is the flow22");
			return "redirect:/fetchEmail";
		} catch (DataIntegrityViolationException e) {
			session.setAttribute("SubmitAuthError", true);
			// Handle data integrity violation (e.g., unique constraint violation)
			return "redirect:/requesterror";
		} catch (DataAccessException e) {
			// Handle generic data access exception
			session.setAttribute("SubmitAuthError", true);
			return "redirect:/requesterror";
		} catch (Exception e) {
			// Handle any other unexpected exceptions
			session.setAttribute("SubmitAuthError", true);
			return "redirect:/requesterror";
		}
	}
}
