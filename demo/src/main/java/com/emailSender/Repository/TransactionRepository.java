package com.emailSender.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailSender.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findByRefId(String upiRefNo);

  
    List<Transaction> findByStatus(boolean status);
    // You can define additional query methods here if needed

}