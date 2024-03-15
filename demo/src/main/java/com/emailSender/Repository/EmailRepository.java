package com.emailSender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailSender.model.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Integer> {

    // You can define additional query methods here if needed


}