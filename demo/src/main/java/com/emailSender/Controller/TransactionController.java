package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.emailSender.Repository.TransactionRepository;
import com.emailSender.Service.TransactionService;

import jakarta.servlet.http.HttpSession;
@Controller
public class TransactionController {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionService transactionService;

    @GetMapping("pay")
    public String pay(Model model, HttpSession session) {
        session.removeAttribute("SubmitAuth");
        session.removeAttribute("SubmitAuthError");
        model.addAttribute("totalAmount", transactionService.calculateTotalAmount());
        model.addAttribute("transactions", transactionRepository.findAll());
        return ("pay");
    }

}
