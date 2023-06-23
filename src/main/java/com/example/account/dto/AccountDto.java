package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

//Entity 클래스와 비슷한데 조금 더 단순화된 버전으로 딱 필요한 것만 넣어놓는 용도?
//03_계좌 생성 API-003 처음부터
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long userId;
    private String accountNumber;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;




    //생성자를 사용하지않고 (Dto는 Entity를 통해서 만들어지는 경우가 가장 많음)
    //특정 Entity -> 특정 Dto 변환
    //이걸로 쓰는게 가독성 좋고 더 안전하게 생성가능
    public static AccountDto fromEntity(Account account){
        return  AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unRegisteredAt(account.getUnRegisteredAt())
                .build();
    }
}
