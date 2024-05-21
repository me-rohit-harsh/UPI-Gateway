package com.emailSender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailSender.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long>  {

}