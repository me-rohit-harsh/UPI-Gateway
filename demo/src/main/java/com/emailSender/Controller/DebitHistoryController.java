package com.emailSender.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.TransactionService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class DebitHistoryController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TransactionService transactionService;

	@GetMapping("/debittransaction")
	public String showDebitTransaction(HttpSession session, Model model) {
		// Check if user is authenticated
		Boolean isAuthenticated = (Boolean) session.getAttribute("auth");
		if (isAuthenticated != null && isAuthenticated) {
			// Get authenticated user ID
			Long userId = (Long) session.getAttribute("userId");
			if (userId != null) {
				// Find user by ID
				User user = userRepository.findById(userId).orElse(null);
				if (user != null) {
					// System.out.println("User" + user);
					model.addAttribute("user", user);

					List<Transaction> transactionList = transactionService.getTransactionsByUserIdAndType(userId,
							"Debit");

					model.addAttribute("transactions", transactionList);

					return "client/debitHistory";
				}
			}
		}

		// Redirect to sign-in page if not authenticated or user not found
		return "client/signin";
	}
}
