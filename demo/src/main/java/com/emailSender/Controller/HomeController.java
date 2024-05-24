package com.emailSender.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	/*
	 * @Autowired private TransactionRepository transactionRepository;
	 * 
	 * @Autowired private EmailService emailService;
	 * 
	 * @Autowired private UserRepository userRepository;
	 */

	@GetMapping("/payment")
	public String showQR(HttpSession session) {
		return ("payment");
	}

	@GetMapping("/mail")
	public String sendMail(HttpSession session) {
		return "mail";
	}

	// @GetMapping("/success")
	// public String success(HttpSession session) {
	// session.removeAttribute("SubmitAuthError");
	// Boolean SubmitAuth = (Boolean) session.getAttribute("SubmitAuth");
	// if (SubmitAuth != null && SubmitAuth) {
	// return "success";
	// } else {
	// return "redirect:/payment";
	// }
	// }

	// @GetMapping("/requesterror")
	// public String error(HttpSession session) {
	// session.removeAttribute("SubmitAuth");
	// Boolean SubmitAuth = (Boolean) session.getAttribute("SubmitAuthError");
	// if (SubmitAuth != null && SubmitAuth) {
	// return "errorPage";
	// } else {
	// return "redirect:/payment";
	// }
	// }

	/*
	In old version i was using this method to save the transactions and after that 
	i was redirecting it to the fetch email to check for payment
	now i am dircelty doing those thing in the email controller of same name mapping 
	 * @PostMapping("/save-utr")
	 * public String saveUtr(@ModelAttribute Transaction transaction, HttpSession
	 * session,
	 * RedirectAttributes redirectAttributes) {
	 * Long userId = (Long) session.getAttribute("userId");
	 * User authUser = userRepository.findById(userId).orElse(null);
	 * // System.out.println("********************");
	 * // System.out.println(authUser);
	 * // System.out.println("********************");
	 * String upiRefNo = transaction.getRefId();
	 * Double amount = transaction.getAmount();
	 * String method = transaction.getMethod();
	 * System.out.println("***********" + upiRefNo + ' ' + amount + ' ' + method +
	 * "***********");
	 * System.out.
	 * println("Tx Id, Amount and Method is coming from the form successfully.");
	 * 
	 * try {
	 * // Check if the UTR already exists in the database
	 * Transaction existingTransaction =
	 * transactionRepository.findByRefId(upiRefNo);
	 * if (existingTransaction != null) {
	 * // UTR already exists, handle accordingly (e.g., show error message)
	 * // UTR was not found, redirect to error page
	 * if (authUser.getEmail() != null) {
	 * emailService.sendSimpleEmail(authUser.getEmail(),
	 * "Payment Rejection - Jixwallet",
	 * "Your payment has already been processed.");
	 * System.out.println("Already Redeemed");
	 * }
	 * 
	 * System.out.println("UTR already exists");
	 * // Redirect to an error page or return a response indicating UTR already
	 * exists
	 * redirectAttributes.addFlashAttribute("errorMsg",
	 * "Your payment has already been processed for Tx ID: "+ upiRefNo);
	 * 
	 * // redirectAttributes.addFlashAttribute("error", true);
	 * return "redirect:/dashboard";
	 * }
	 * System.out.println("No any existing UTR found next step is to save the tx ");
	 * // Create a new Transaction object
	 * Transaction newTransaction = new Transaction();
	 * 
	 * newTransaction.setRefId(upiRefNo);
	 * // newTransaction.setStatus();
	 * newTransaction.setAmount(amount);
	 * newTransaction.setMethod(method);
	 * if (authUser == null) {
	 * redirectAttributes.addFlashAttribute("errorMsg",
	 * "Please Login to Continue!");
	 * return "redirect:/signin";
	 * }
	 * newTransaction.setUser(authUser);
	 * newTransaction.setType("Credit");
	 * System.out.println(newTransaction);
	 * // Save the new transaction object to the database
	 * transactionRepository.save(newTransaction);
	 * System.out.println("Transaction has been saved successfully!");
	 * session.setAttribute("submittedUtr", newTransaction.getRefId());
	 * session.setAttribute("moneySent", newTransaction.getAmount());
	 * session.setAttribute("newTransaction", newTransaction);
	 * // session.setAttribute("userEmail", newTransaction.getEmail());
	 * // Redirect to the fetchEmail page to check for valid UTR
	 * 
	 * System.out.println("Redirceting to the fetchEmail!");
	 * return "redirect:/fetchEmail";
	 * } catch (DataIntegrityViolationException e) {
	 * // Handle data integrity violation (e.g., unique constraint violation)
	 * redirectAttributes.addFlashAttribute("errorMsg", "Payment has been failed!");
	 * 
	 * // redirectAttributes.addFlashAttribute("error", true);
	 * return "redirect:/dashboard";
	 * } catch (DataAccessException e) {
	 * // Handle generic data access exception
	 * redirectAttributes.addFlashAttribute("errorMsg", "Payment has been failed!");
	 * 
	 * // redirectAttributes.addFlashAttribute("error", true);
	 * return "redirect:/dashboard";
	 * } catch (Exception e) {
	 * // Handle any other unexpected exceptions
	 * redirectAttributes.addFlashAttribute("errorMsg",
	 * "Payment has been failed! Please try again");
	 * 
	 * // redirectAttributes.addFlashAttribute("error", true);
	 * return "redirect:/dashboard";
	 * }
	 * }
	 */
}
