package com.example.account.dto;

import lombok.*;
//Controller 와 accountService 간의 데이터를 주고받는게 최적화된 Dto

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo {
    private String accountNumber;
    private Long balance;


}
