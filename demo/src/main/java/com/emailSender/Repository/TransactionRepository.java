package com.emailSender.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emailSender.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction findByRefId(String upiRefNo);

    List<Transaction> findByStatusAndType(boolean status, String type);

    List<Transaction> findByStatus(boolean status);

    // You can define additional query methods here if needed
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndType(Long userId, String type);

    List<Transaction> findByStatusAndUserId(boolean b, Long userId);

    List<Transaction> findByMethodIn(List<String> methods);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.method IN ('USDT (BEP20)', 'USDT (TRC20)', 'USDT (BNB)') AND t.status = true")
    Double getSumOfSuccessfulUsdtTransactions();
}