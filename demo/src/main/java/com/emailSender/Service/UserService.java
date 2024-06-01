package com.emailSender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emailSender.Repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Double getTotalBalance() {
        return userRepository.findTotalBalance();
    }
}
