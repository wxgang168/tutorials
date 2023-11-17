package com.baeldung.telegram;


import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    
    @Column(name = "uid")
    protected Long uid;
    
    @Column(name = "chatid")
    protected Long chatid;
    
    @Column
    protected BigDecimal income;

    @CreatedDate
    protected Date createdDate = new Date();

    public Date getCreatedDate() {
        return createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getChatid() {
		return chatid;
	}

	public void setChatid(Long chatid) {
		this.chatid = chatid;
	}
    
}
