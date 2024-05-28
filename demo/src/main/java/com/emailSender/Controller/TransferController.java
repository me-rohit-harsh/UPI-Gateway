package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransferController {
	@Autowired
	private UserRepository userRepository;

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

	@PostMapping("/transferMoney")
	public String transferMoney(@RequestParam("balance") double balance, @RequestParam("username") String username,
	                            RedirectAttributes redirectAttributes, HttpSession session) {
	    User beneficiaryUser = userRepository.findByUsername(username);
	    User user = userRepository.findById((Long) session.getAttribute("userId")).orElse(null);

	    if (beneficiaryUser != null && user != null) {
	        if (user.getBalance() >= balance) {
	            // Deduct balance from the user
	            user.setBalance(user.getBalance() - balance);
	            // Add balance to the beneficiary
	            beneficiaryUser.setBalance(beneficiaryUser.getBalance() + balance);

	            // Save the updated users
	            userRepository.save(user);
	            userRepository.save(beneficiaryUser);

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
