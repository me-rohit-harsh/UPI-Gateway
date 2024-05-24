package com.emailSender.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.emailSender.Repository.BankRepository;
import com.emailSender.model.Bank;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    public Bank getLatestBankDetailsForCurrentUser(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 1); // Fetch only the latest entry
        List<Bank> banks = bankRepository.findLatestByUserId(userId, pageRequest);
        return banks.isEmpty() ? null : banks.get(0);
    }

    public void saveBankDetails(Bank bank) {
        bankRepository.save(bank);
    }
}
