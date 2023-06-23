package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository //Transaction 라는 테이블에 접속하기 위한 인터페이스
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Optional<Transaction> findByTransactionId(String transactionId);
}
