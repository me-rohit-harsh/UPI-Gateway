package com.emailSender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emailSender.Repository.UserRepository;
import com.emailSender.model.User;

@Service
public class WalletService {
    @Autowired
    private UserRepository userRepository;

    public boolean processTransaction(User user, double amount) {
       
        if (user != null && user.getBalance() >= amount) {
            user.setBalance((user.getBalance() - amount));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
