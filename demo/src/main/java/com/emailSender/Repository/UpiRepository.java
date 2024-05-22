package com.emailSender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailSender.model.UPI;

@Repository
public interface UpiRepository extends JpaRepository<UPI, Long> {

}
