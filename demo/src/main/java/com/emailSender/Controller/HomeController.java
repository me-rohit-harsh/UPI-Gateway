package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/payment")
	public String showQR() {
		return ("payment");
	}

	@GetMapping("pay")
	public String pay() {
		return ("pay");
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
	public String saveUtr(@RequestParam("upiRefNo") String upiRefNo, @RequestParam("txAmmount") Float txAmmount,
			@RequestParam("email") String email, HttpSession session) {
		session.setAttribute("SubmitAuth", true);
		try {
			// Check if the UTR already exists in the database
			User existingUser = userRepository.findByRefId(upiRefNo);
			if (existingUser != null) {
				// UTR already exists, handle accordingly (e.g., show error message)
				System.out.println("Already Redeemed");
				session.setAttribute("SubmitAuthError", true);

				// Redirect to an error page or return a response indicating UTR already exists
				return "redirect:/requesterror";
			}

			// Create a new User object
			User newUser = new User();
			session.setAttribute("newUser", newUser);
			newUser.setRefId(upiRefNo);
			newUser.setStatus(false);
			newUser.setTxAmmount((int) txAmmount.floatValue());
			newUser.setEmail(email);
			// Save the new User object to the database
			userRepository.save(newUser);
			session.setAttribute("submittedUtr", newUser.getRefId());
			session.setAttribute("moneySent", newUser.getTxAmmount());
			session.setAttribute("userEmail", newUser.getEmail());
			// Redirect to the fetchEmail page to check for valid UTR
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
