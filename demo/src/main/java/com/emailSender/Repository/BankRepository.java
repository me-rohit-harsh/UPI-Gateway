package com.emailSender.Repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emailSender.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    // Set<Bank> findByUserId(Long userId);
    List<Bank> findByUserId(Long userId);

    @Query("SELECT b FROM Bank b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Bank> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);

}
