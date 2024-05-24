package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.UpiService;
import com.emailSender.model.UPI;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentMethodController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpiService upiService;

    @PostMapping("/saveUpiDetails")
    public String saveUpiDetails(@ModelAttribute("upiDetails") UPI upi, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findById((Long) session.getAttribute("userId")).orElse(null);
        UPI newUpi = new UPI();
        newUpi.setHolderName(upi.getHolderName());
        newUpi.setUpiID(upi.getUpiID());
        newUpi.setUser(user);
        newUpi.setQrCode(upi.getQrCode());
        upiService.saveUpiDetails(newUpi); // Save the UPI details
        redirectAttributes.addFlashAttribute("message", "UPI details has been updated");
        return "redirect:/method"; // Redirect to the UPI form page
    }

}
