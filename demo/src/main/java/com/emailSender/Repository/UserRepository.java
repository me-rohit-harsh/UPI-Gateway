package com.emailSender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailSender.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByRefId(String upiRefNo);
    // You can define additional query methods here if needed


}