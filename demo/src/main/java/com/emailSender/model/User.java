package com.emailSender.model;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;


@Table
@Entity
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Size(min = 3, max = 12)
	@Column(unique = true, nullable = false)
	private String username;
	
	private String name;

	@Column(unique = true, nullable = false)
	private String email;
	@NotEmpty
	private String password;

	private String address;

	private Double balance;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private Set<Transaction> transactions;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Bank> banks;

//	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//	private Set<Email> emails;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Set<Bank> getBanks() {
		return banks;
	}

	public void setBanks(Set<Bank> banks) {
		this.banks = banks;
	}

//	public Set<Email> getEmails() {
//		return emails;
//	}
//
//	public void setEmails(Set<Email> emails) {
//		this.emails = emails;
//	}

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

	


	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", name=" + name + ", email=" + email + ", password="
				+ password + ", address=" + address + ", balance=" + balance + ", transactions=" + transactions
				+ ", banks=" + banks + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	public User(Long id, @NotEmpty @Size(min = 3, max = 12) String username, String name, String email,
			@NotEmpty String password, String address, Double balance, Set<Transaction> transactions, Set<Bank> banks,
			Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.email = email;
		this.password = password;
		this.address = address;
		this.balance = balance;
		this.transactions = transactions;
		this.banks = banks;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
