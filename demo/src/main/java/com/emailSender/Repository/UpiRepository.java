package com.emailSender.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.emailSender.model.UPI;

@Repository
public interface UpiRepository extends JpaRepository<UPI, Long> {
    @Query("SELECT u FROM UPI u WHERE u.user.id = :userId ORDER BY u.createdAt DESC")
    List<UPI> findLatestByUserId(@Param("userId") Long userId, Pageable pageable);
}

