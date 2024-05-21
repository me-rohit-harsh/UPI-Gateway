package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
public class HomeController {
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/payment")
	public String showQR(HttpSession session) {
		session.removeAttribute("SubmitAuth");
		session.removeAttribute("SubmitAuthError");
		return ("payment");
	}

	@GetMapping("/mail")
	public String sendMail(HttpSession session) {
		session.removeAttribute("SubmitAuth");
		session.removeAttribute("SubmitAuthError");
		// Check if user is authenticated
		// Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
		// if (isAuthenticated != null && isAuthenticated) {
		// // Get authenticated user ID
		// Long userId = (Long) session.getAttribute("userId");
		// if (userId != null) {
		// // Find user by ID
		// User user = userRepository.findById(userId).orElse(null);
		// if (user != null) {
		// return "mail";
		//
		// }
		// }
		// }
		// return "redirect:/signin";
		return "mail";
	}

	@GetMapping("/success")
	public String success(HttpSession session) {
		session.removeAttribute("SubmitAuthError");
		Boolean SubmitAuth = (Boolean) session.getAttribute("SubmitAuth");
		if (SubmitAuth != null && SubmitAuth) {
			return "success";
		} else {
			return "redirect:/payment";
		}
	}

	@GetMapping("/requesterror")
	public String error(HttpSession session) {
		session.removeAttribute("SubmitAuth");
		Boolean SubmitAuth = (Boolean) session.getAttribute("SubmitAuthError");
		if (SubmitAuth != null && SubmitAuth) {
			return "errorPage";
		} else {
			return "redirect:/payment";
		}
	}

	@PostMapping("/save-utr")
	public String saveUtr(@ModelAttribute Transaction transaction, HttpSession session, RedirectAttributes redirectAttributes) {
		session.setAttribute("SubmitAuth", true);
		Long userId= (Long) session.getAttribute("userId");
		User authUser = userRepository.findById(userId).orElse(null);
		// System.out.println("********************");
		// System.out.println(authUser);
		// System.out.println("********************");
		String upiRefNo = transaction.getRefId();
		Double amount = transaction.getAmount();
		String method = transaction.getMethod();
		System.out.println("***********" + upiRefNo + ' ' + amount + ' ' + method + "***********");
		System.out.println("Tx Id, Amount and Method is coming from the form successfully.");

		try {
			// Check if the UTR already exists in the database
			Transaction existingTransaction = transactionRepository.findByRefId(upiRefNo);
			if (existingTransaction != null) {
				// UTR already exists, handle accordingly (e.g., show error message)
				// UTR was not found, redirect to error page
				if (authUser.getEmail() != null) {
					emailService.sendSimpleEmail(authUser.getEmail(), "Payment Rejection - Jixwallet",
							"Your payment has already been processed.");
					System.out.println("Already Redeemed");
				}
				session.setAttribute("SubmitAuthError", true);

				System.out.println("UTR already exists");
				// Redirect to an error page or return a response indicating UTR already exists
				return "redirect:/requesterror";
			}
			System.out.println("No any existing UTR found next step is to save the tx ");
			// Create a new Transaction object
			Transaction newTransaction = new Transaction();
			session.setAttribute("newTransaction", newTransaction);
			newTransaction.setRefId(upiRefNo);
			newTransaction.setStatus(false);
			newTransaction.setAmount(amount);
			newTransaction.setMethod(method);
			if(authUser==null){
				redirectAttributes.addFlashAttribute("errorMsg", "Please Login to Continue!");
				return "redirect:/signin";
			}
			newTransaction.setUser(authUser);
			newTransaction.setType("Credit");
			System.out.println(newTransaction);
			// Save the new transaction object to the database
			transactionRepository.save(newTransaction);
			System.out.println("Transaction has been saved successfully!");
			session.setAttribute("submittedUtr", newTransaction.getRefId());
			session.setAttribute("moneySent", newTransaction.getAmount());
			// session.setAttribute("userEmail", newTransaction.getEmail());
			// Redirect to the fetchEmail page to check for valid UTR

			System.out.println("Redirceting to the fetchEmail!");
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
