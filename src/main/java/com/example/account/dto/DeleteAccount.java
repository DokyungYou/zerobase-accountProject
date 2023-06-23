package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {

    // 이너클래스로 만들면 이름을 지을 때 좀 더 명시적으로 알아보기가 좋다
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request{

        @NotNull
        @Min(1)  // 0은 없고 1부터 시작하기로 함
        private Long userId;

        @NotBlank  //NotNull 보다 강력함
        @Size(min = 10, max = 10)
        private String accountNumber;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber; //계좌번호
        private LocalDateTime unRegisteredAt; //등록일시


        public static  Response from(AccountDto accountdto){
            return  Response.builder()
                    .userId(accountdto.getUserId())
                    .accountNumber(accountdto.getAccountNumber())
                    .unRegisteredAt(accountdto.getRegisteredAt())
                    .build();
        }
    }
}
