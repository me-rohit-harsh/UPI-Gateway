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


@Data
@Table
@Entity
public class Email {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String sentFrom;

	private String subject;

	private String text;
	@Column(columnDefinition = "TEXT")
	private String toAddress;

	private Boolean status;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

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

	public String getSentFrom() {
		return sentFrom;
	}

	public void setSentFrom(String sentFrom) {
		this.sentFrom = sentFrom;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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



	public Email(Long id, String sentFrom, String subject, String text, String toAddress, Boolean status,
			Date createdAt, Date updatedAt) {
		super();
		this.id = id;
		this.sentFrom = sentFrom;
		this.subject = subject;
		this.text = text;
		this.toAddress = toAddress;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Email [id=" + id + ", sentFrom=" + sentFrom + ", subject=" + subject + ", text=" + text + ", toAddress="
				+ toAddress + ", status=" + status + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	public Email() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
