package com.example.account.repository;

import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository //AccountUser 라는 테이블에 접속하기 위한 인터페이스
public interface AccountUserRepository extends JpaRepository<AccountUser,Long> {
}
