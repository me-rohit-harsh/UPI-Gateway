package com.emailSender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailSender.Service.WalletService;
import com.emailSender.model.User;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping("/process")
    public ResponseEntity<String> processTransaction(@RequestBody User request) {
        boolean result = walletService.processTransaction(request.getUsername(), request.getSecCode(),
                request.getBalance());
        if (result) {
            return ResponseEntity.ok("Transaction successful");
        } else {
            return ResponseEntity.status(400).body("Transaction failed");
        }
    }
}
