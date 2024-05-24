package com.emailSender.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emailSender.Repository.UpiRepository;
import com.emailSender.Repository.UserRepository;
import com.emailSender.Service.UpiService;
import com.emailSender.model.UPI;
import com.emailSender.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PaymentMethodController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpiService upiService;

    @Autowired
    private UpiRepository upiRepository;



}
