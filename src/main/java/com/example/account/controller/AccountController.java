package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController { //외부에선 컨트롤러로만, 컨트롤러는 서비스, 서비스는 레파지토리로 접속하는 순차적인 계층화된 구조
    private final AccountService accountService;


    //파라미터로  String userId, Integer initialBalance 이런식으로 받을 수도 있으나

    //@RequestBody를 사용하는 방식으로 진행
    @PostMapping("/account") //웹 주소에 localhost:8080/create-account -> 아래 메소드 실행
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
            ){
        AccountDto accountDto = accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance()
        );

        return CreateAccount.Response.from(accountDto);
    }



    @DeleteMapping ("/account") //웹 주소에 localhost:8080/create-account -> 아래 메소드 실행
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ){
       return  DeleteAccount.Response.from(
               accountService.deleteAccount(
                       request.getUserId(),
                       request.getAccountNumber()
               )
       );
    }


    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId
    ){
      return accountService.getAccountByUserId(userId)
              .stream().map(accountDto ->
                      AccountInfo.builder()
                      .accountNumber(accountDto.getAccountNumber())
                      .balance(accountDto.getBalance()).build())
              .collect(Collectors.toList());

        }



    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id){ //이름이 같으면 @PathVariable 생략 가능하나 명시적으로 넣어줌

        return accountService.getAccount(id);
        //웹 화면에 {"id":1,"accountNumber":"40000","accountStatus":"IN_USE"} 이런식으로 뜨네
    }
}
