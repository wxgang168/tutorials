package com.baeldung.telegram;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Serializable> {
	List<Account> findAllByCreatedDateBetween(
		      Date createdDateStart,
		      Date createdDateEnd);

	List<Account> findAllByChatid(long chatId);
}
