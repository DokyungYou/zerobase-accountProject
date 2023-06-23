package com.example.account.domain;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity //클래스가 데이터베이스 테이블과 매핑되는 엔티티(Entity)임을 나타냄 (일종의 설정클래스, 자바 객체 X, 하나의 테이블을 만든 것)
public class Account extends BaseEntity { //Account라는 테이블을 만든 것임

    @ManyToOne //  1:N관계
    private AccountUser accountUser;  // h2 시스템에 있는 user 테이블이랑 충돌될 수 있기때문에 accountUser로 지음
    private String accountNumber;

    @Enumerated(EnumType.STRING) //enum값의 실제 문자열이름을 그대로 db에 저장하게끔 함
    private AccountStatus accountStatus;
    //enum타입은 그냥 두면 실제 내부적으로는 순서대로 0,1,2...그런거를 db에 저장하면 실제 무슨 값인지 파악하기가 어렵다.
    //따라서 @Enumerated(EnumType.STRING)를 붙여줌

    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;


    // 밸런스를 변경하는 로직은 엔티티에 안에 넣어주는 것도 일종의 안전한 방법
    // 중요한 데이터를 변경하는 로직은 객체 안에서 직접 수행할 수 있도록
    // (처음엔 service 에서 작성했다가 수정)
    public void useBalance(Long amount){
        if(amount > balance){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;

    }

    public void cancelBalance(Long amount){
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        balance += amount;

    }


}
