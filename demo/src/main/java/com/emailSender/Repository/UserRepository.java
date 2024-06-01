package com.emailSender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emailSender.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // User findByRefId(String upiRefNo);
    // You can define additional query methods here if needed
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsernameOrEmail(String username, String email);

    User findByUsernameAndSecCode(String username, String secCode);

    User findByUsername(String username);

    @Query("SELECT SUM(u.balance) FROM User u")
    Double findTotalBalance();
}