package com.emailSender.Controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.EmailService;
import com.emailSender.model.Transaction;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransferController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private EmailService emailService;

	@PostMapping("/findbeneficiary")
	public String findBeneficiary(@RequestParam("userId") String userId, HttpSession session,
			RedirectAttributes redirectAttributes) {
		Boolean exist = userRepository.existsByUsername(userId);
		if (exist) {
			User user = userRepository.findByUsername(userId);
			redirectAttributes.addFlashAttribute("beneficiaryName", user.getEmail());
			redirectAttributes.addFlashAttribute("beneficiaryId", userId);
			System.out.println(user.getEmail() + userId);
			redirectAttributes.addFlashAttribute("message", "User Found!!");
		} else {
			redirectAttributes.addFlashAttribute("errorMsg", "Benificiary Not Found");
		}
		return "redirect:/p2p";
	}

	public String transferMoney(Double balance, String username, User user,
			RedirectAttributes redirectAttributes) {
		User beneficiaryUser = userRepository.findByUsername(username);
		if (beneficiaryUser != null && user != null) {
			if (user.getBalance() >= balance) {
				// Deduct balance from the user
				user.setBalance(user.getBalance() - balance);
				// Add balance to the beneficiary
				beneficiaryUser.setBalance(beneficiaryUser.getBalance() + balance);

				// Save the updated users
				userRepository.save(user);
				userRepository.save(beneficiaryUser);
				// Create a debit transaction for the user
				Transaction userTransaction = new Transaction();
				userTransaction.setUser(user);
				userTransaction.setAmount(balance);
				userTransaction.setType("Debit");
				userTransaction.setStatus(true);
				userTransaction.setRefId(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
				userTransaction.setMethod("Wallet Transfer");

				// Create a credit transaction for the beneficiary
				Transaction beneficiaryTransaction = new Transaction();
				beneficiaryTransaction.setRefId(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
				beneficiaryTransaction.setUser(beneficiaryUser);
				beneficiaryTransaction.setAmount(balance);
				beneficiaryTransaction.setType("Credit");
				beneficiaryTransaction.setStatus(true);
				beneficiaryTransaction.setMethod("Wallet Transfer");

				// Save the transactions
				transactionRepository.save(userTransaction);
				transactionRepository.save(beneficiaryTransaction);
				String userEmailBody = String.format(
						"Dear %s,\n\n" +
								"We are pleased to inform you that your recent payment has been successfully processed.\n\n"
								+
								"Details of the transaction are as follows:\n" +
								"Amount: %.2f\n" +
								"To: %s\n" +
								"Date and Time: %s\n" +
								"Payment Method: %s\n\n" +
								"Thank you for using our services!\n\n" +
								"Best regards,\n" +
								"JixWallet",
						user.getUsername(), balance,
						username,
						userTransaction.getCreatedAt().toString(), userTransaction.getMethod());

				String benEmailBody = String.format(
						"Dear %s,\n\n" +
								"We are pleased to inform you that you have received a payment.\n\n" +
								"Details of the transaction are as follows:\n" +
								"Amount: %.2f\n" +
								"From: %s\n" +
								"Date and Time: %s\n" +
								"Payment Method: %s\n\n" +
								"Thank you for using our services!\n\n" +
								"Best regards,\n" +
								"JixWallet",
						username, balance, user.getUsername(),
						beneficiaryTransaction.getCreatedAt().toString(), beneficiaryTransaction.getMethod());

				emailService.sendSimpleEmail(user.getEmail(), "Transaction Successful - JixWallet", userEmailBody);
				emailService.sendSimpleEmail(beneficiaryUser.getEmail(), "Credit Alert - JixWallet", benEmailBody);

				redirectAttributes.addFlashAttribute("message", "Amount Transferred successfully!!");
			} else {
				redirectAttributes.addFlashAttribute("errorMsg", "Insufficient balance to transfer the amount!");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorMsg", "Beneficiary Not Found!");
		}

		return "redirect:/p2p";
	}

}
