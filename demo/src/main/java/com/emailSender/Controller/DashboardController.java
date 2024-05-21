package com.emailSender.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.TransactionService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
  @Autowired
	TransactionRepository transactionRepository;
	@Autowired
	TransactionService transactionService;
	@Autowired
	UserRepository userRepository;

	@GetMapping("/dashboard")
	public String pay(Model model, HttpSession session) {
		// Remove previous session attributes

		// Check if user is authenticated
		Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
		if (isAuthenticated != null && isAuthenticated) {
			// Get authenticated user ID
			Long userId = (Long) session.getAttribute("userId");
			if (userId != null) {
				// Find user by ID
				User user = userRepository.findById(userId).orElse(null);
				if (user != null) {
//					System.out.println("User" + user);
					model.addAttribute("user", user);
					// System.err.println(model.getAttribute("user"));
					model.addAttribute("totalAmount", transactionService.calculateTotalAmount(userId));

					List<Transaction> transactionList = transactionService.getTransactionsByUserId(userId);
					// model.addAttribute("transactions", TransactionList);
					// System.err.println(TransactionList.size());
					model.addAttribute("transactions", transactionList);
					// System.out.println(model.getAttribute("transactions"));
					return "client/dashboard"; // Make sure this corresponds to your actual Thymeleaf template for the payment page
				}
			}
		}

		// Redirect to sign-in page if not authenticated or user not found
		return "redirect:/signin";
	}


}
