package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;

import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //꼭 필요한 argument 가 들어간 생성자
public class AccountService { //AccountRepository 를 활용하여 데이터를 저장하도록 함
    private final AccountRepository accountRepository;
    // -> 이 값은 생성자가 아니면 담을 수 없음(나중에 수정 불가)
    // 무조건 생성자에 포함돼있어야 함 ( 현재는 @RequiredArgsConstructor 로 해결)

    //내가 만든 bean에 다른 bean를 넣어주고싶을 땐 final로 잡아주고, @RequiredArgsConstructor 로

    private final AccountUserRepository accountUserRepository;



    /**
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성하고
     * 계좌를 저장하고, 그 정보를 넘긴다.
     */

    // 리턴타입을 그냥 Account로 사용하지 않는 이유 03_계좌 생성 API-003 처음부분부터 나옴
    @Transactional //Account라는 테이블에 데이터를 저장
    public AccountDto createAccount(Long userId, Long initialBalance){

        AccountUser accountUser = getAccountUser(userId);

        //이런 유효성 코드는 따로 private method 로 빼는 것이 좋음 (다른 코드 읽기가 어려움)
        //ctrl + shift + m
        validate(accountUser);


        //이제 계좌번호생성?  가장 마지막에 생성된 계좌를 가져오고 그 계좌의 계좌번호보다 하나 더 큰 숫자를 넣어줄 것임
        //본래 .toString썻는데 그러면 .orElse() 안나와서 + ""로 문자열화
        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");

        Account account = accountRepository.save(
                 Account.builder()
                         .accountUser(accountUser)
                         .accountStatus(AccountStatus.IN_USE) 
                         .accountNumber(newAccountNumber)
                         .balance(initialBalance)
                         .registeredAt(LocalDateTime.now())
                         .build()
         );

        return AccountDto.fromEntity(account);

    }


    //ctrl + shift + m 로 생성된 메소드
    private void validate(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) >= 10){
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }


    @Transactional
    public Account getAccount(Long id){
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = getAccountUser(userId);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        accountRepository.save(account);

        return  AccountDto.fromEntity(account);
    }


    //추출 메소드  - 중복코드 찾기 (ctrl + shift + f)
    private AccountUser getAccountUser(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)  //조회를 했을 때 나오는 기본적인 타입이 옵셔널이다
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));//옵셔널에서 데이터가 없을때 에러를 뱉고 데이터가 있을때는 그 값을 준다
        return accountUser;
    }


    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() > 0){
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }


    @Transactional
    public List<AccountDto> getAccountByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        return  accounts.stream()
                .map(AccountDto::fromEntity) //or  .map(account -> AccountDto.fromEntity(account))
                .collect(Collectors.toList());
    }
}
