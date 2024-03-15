package com.emailSender.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table
public class Transaction {
    @Id
    @Column(name = "Transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email_id")
    private String email;

    @Column(name = "Tx_ammount")
    private Integer ammount;

    @Column(name = "Tx_status")
    private Boolean status;

    @Column(name = "Ref_id", unique = true)
    private String refId;

    @Column(name = "Transaction_type")
    private String type;
    
    @Column(name = "Method")
    private String method;

    // Automatically set to the current timestamp when the entity is created
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    // Automatically updated to the current timestamp when the entity is updated
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

}
