package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class OtpController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailController emailController;

	@GetMapping("/showCode")
	@Async
	public void showCode(HttpSession session) {
		// Generate a random 4-digit OTP
		int OTP = (int) (Math.random() * 9000) + 1000;

		// You may need to modify the logic to get user details based on your
		// application
		Long userId = (Long) session.getAttribute("userId");
		User user = userRepository.findById(userId).orElse(null);
		user.setOtp(OTP);
		userRepository.save(user);
		// Send OTP to user's email
		emailController.sendOTP(user.getEmail(), "Your OTP for Verification", "Your OTP is: " + OTP);
	}

	@PostMapping("/checkOtp")
	public String checkOtp(@RequestParam("d1") Integer d1,
			@RequestParam("d2") Integer d2,
			@RequestParam("d3") Integer d3,
			@RequestParam("d4") Integer d4,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		System.out.println(" i am here where are you??");
		String otp = String.valueOf(d1) + String.valueOf(d2) + String.valueOf(d3) + String.valueOf(d4);
		System.out.println("OTP is:" + otp);
		Long userId = (Long) session.getAttribute("userId");
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			redirectAttributes.addFlashAttribute("errorMsg", "User not found!");
			return "redirect:/profile";
		}

		int userOTP = user.getOtp();
	
		if (otp.equals(String.valueOf(userOTP))) {
			user.setOtp(null);
			userRepository.save(user);
			redirectAttributes.addFlashAttribute("message", "OTP Verification Success!");

			redirectAttributes.addFlashAttribute("securityCode", user.getSecCode());
			return "redirect:/profile";
		} else {
			redirectAttributes.addFlashAttribute("errorMsg", "You have entered incorrect OTP!");
			return "redirect:/profile";
		}
	}

}