package com.emailSender.Repository;

import org.springframework.stereotype.Repository;

import com.emailSender.model.Contact;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Long>{
    
}
