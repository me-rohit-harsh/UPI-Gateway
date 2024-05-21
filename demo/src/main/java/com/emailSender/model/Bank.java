package com.emailSender.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table
public class Bank {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String holderName;

	@Column(unique = true, nullable = false)
	private String accountNo;

	private String branch;

	private String ifsc;

	private String securityCode;

	private Date lastLogin;

	@Column(unique = true, nullable = false)
	private String upiId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", updatable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getUpiId() {
		return upiId;
	}

	public void setUpiId(String upiId) {
		this.upiId = upiId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Bank(Long id, String holderName, String accountNo, String branch, String ifsc, String upiId, User user,
			Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.holderName = holderName;
		this.accountNo = accountNo;
		this.branch = branch;
		this.ifsc = ifsc;
		this.upiId = upiId;
		this.user = user;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Bank [id=" + id + ", holderName=" + holderName + ", accountNo=" + accountNo + ", branch=" + branch
				+ ", ifsc=" + ifsc + ", upiId=" + upiId + ", user=" + user + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}

	public Bank() {
		super();
		// TODO Auto-generated constructor stub
	}

}
