package com.emailSender.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.emailSender.model.Transaction;

import com.emailSender.Repository.TransactionRepository;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsByStatus(boolean status) {
        return transactionRepository.findByStatus(status);
    }
    
    public double calculateTotalAmount(Long userId) {
        List<Transaction> transactions = transactionRepository.findByStatusAndUserId(true, userId);
        double totalAmount = 0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

}
