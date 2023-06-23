package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository //Account 라는 테이블에 접속하기 위한 인터페이스
public interface AccountRepository extends JpaRepository<Account,Long> {

    //Id 기준으로 내림차순 정렬한 값에서 맨첫번째값을 가져올 것임
    //이름을 형식에 맞춰 쓰기만하면 자동으로 쿼리를 생성해준다고 함
    //근데 아직 값이 없는 상태일 수도 있어서(계좌가 아직 하나도 없을 경우) 이렇게 만들었다고? 03_계좌 생성 API-002 20:00
    Optional<Account> findFirstByOrderByIdDesc(); //인터페이스라 구현부는 X
    //이미 JpaRepository<>클래스 내에 findBy()...등의 메소드가 있고 우리 편의대로 만든 메소드를 추가한 것임


    Integer countByAccountUser(AccountUser accountUser);

    Optional<Account> findByAccountNumber(String AccountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);
}
