package com.baeldung.telegram;


import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Column
    protected Long uid;
    
    @Column
    protected String name;

    @CreatedDate
    @Column
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
