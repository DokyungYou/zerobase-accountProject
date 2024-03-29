package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;

import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    public static final long CANCEL_AMOUNT = 200L;


    
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;


    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
     void successUseBalance(){
     //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);



        //when (method)
        TransactionDto transactionDto = transactionService.useBalance(1L,
                "1000000000",
                2340L);


        //then (assertEquals)
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(2340L, captor.getValue().getAmount());
        assertEquals(7660L, captor.getValue().getBalanceSnapshot());

        assertEquals(TransactionResultType.S,transactionDto.getTransactionResultType());
        assertEquals(TransactionType.USE,transactionDto.getTransactionType());
        assertEquals(9000L, transactionDto.getBalanceSnapshot());
        assertEquals(1000L, transactionDto.getAmount());
     }


    @Test
    @DisplayName("해당 유저 없음 - 잔액 사용 실패")
    void useBalance_UserNotFound(){
        //given (parameter)

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        1L,"1000000000",1000L));

        //then (assertEquals)
        assertEquals(ErrorCode.USER_NOT_FOUND,exception.getErrorCode());
    }




    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 실패")
    void deleteAccount_AccountNotFound(){
        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1000000000",1000L)); //여기 1000L맞나

        //then (assertEquals)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }



    @Test
    @DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
    void deleteAccountFailed_userUnMatch(){
        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        AccountUser otherUser = AccountUser.builder()
                .name("Park").build();
        user.setId(13L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(otherUser)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1234567890",1000L));

        //then (assertEquals)
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());

    }


    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 함")
    void deleteAccountFailed_balanceNotEmpty(){
//given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(123L)
                        .accountNumber("1000000012").build()));

// when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1234567890",1000L));


// then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }



    @Test
    @DisplayName("해지 계좌는 사용할 수 없다.")
    void deleteAccountFailed_alreadyUnregistered(){
        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountStatus(AccountStatus.UNREGISTERED) //이미 해지상태라면
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1234567890",1000L));

        //then (assertEquals)
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());

    }


    @Test
    @DisplayName("거래 금액이 잔액보다 큰 경우")
    void exceedAmount_UseBalance(){
        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(100L)
                .accountNumber("1000000012").build();


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));




        //when (method)
        //then
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,"1234567890",1000L)); //1000원을 쓰려고했다


        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
        verify(transactionRepository,times(0)).save(any());

    }


    @Test
    @DisplayName("실패 트랜잭션 저장 성공")
    void saveFailedUseTransaction(){
        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);



        //when (method)
         transactionService.saveFailedUseTransaction("1000000000", 2340L);


        //then (assertEquals)
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(2340L, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapshot());
        assertEquals(TransactionResultType.F, captor.getValue().getTransactionResultType());


    }


    @Test
    void successCancelBalance(){
        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();

    Transaction transaction = Transaction.builder()
            .account(account)
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.S)
            .transactionId("transactionId")
            .transactedAt(LocalDateTime.now())
            .amount(CANCEL_AMOUNT)
            .balanceSnapshot(9000L)
            .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.CANCEL)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionIdForCancel")
                        .transactedAt(LocalDateTime.now())
                        .amount(CANCEL_AMOUNT)
                        .balanceSnapshot(10000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);



        //when (method)
        TransactionDto transactionDto = transactionService.cancelBalance("transactionId",
                "1000000000",
                CANCEL_AMOUNT);


        //then (assertEquals)
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(CANCEL_AMOUNT, captor.getValue().getAmount());
        assertEquals(10000L + CANCEL_AMOUNT, captor.getValue().getBalanceSnapshot());

        assertEquals(TransactionResultType.S,transactionDto.getTransactionResultType());
        assertEquals(TransactionType.CANCEL,transactionDto.getTransactionType());
        assertEquals(10000L, transactionDto.getBalanceSnapshot());
        assertEquals(CANCEL_AMOUNT, transactionDto.getAmount());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 취소 실패")
    void cancelTransaction_AccountNotFound(){
        //given (parameter)


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(  //강의에선 지웠음
                        Transaction.builder()
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(CANCEL_AMOUNT)
                        .balanceSnapshot(9000L)
                        .build()));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1000000000",CANCEL_AMOUNT));

        //then (assertEquals)
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }



    @Test
    @DisplayName("원 사용 거래 없음 - 잔액 사용 취소 실패")
    void cancelTransaction_TransactionNotFound(){
        //given (parameter)


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1000000000",1000L)); //여기 1000L맞나

        //then (assertEquals)
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());

    }


    @Test
    @DisplayName("거래와 계좌가 매칭실패 - 잔액 사용 취소 실패")
    void cancelTransaction_TransactionAccountUnMatch(){

        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        account.setId(1L);

        Account accountNotUse = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000013").build();
        accountNotUse.setId(2L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));



        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(accountNotUse));


        
        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1000000000",CANCEL_AMOUNT)); //여기 1000L맞나

        //then (assertEquals)
        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH, exception.getErrorCode());

    }



    @Test
    @DisplayName("취소는 1년까지만 가능 - 잔액 사용 취소 실패")
    void cancelTransaction_TooOldOrder(){

        //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));



        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId","1000000000",CANCEL_AMOUNT)); //여기 1000L맞나

        //then (assertEquals)
        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());

    }


    @Test
     void successQueryTransaction(){
     //given (parameter)
        AccountUser user = AccountUser.builder()
                .name("You").build();

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .accountNumber("1000000012").build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        //when (method)
        TransactionDto transactionDto = transactionService.queryTransaction("trxId");

        //then (assertEquals)
        assertEquals(TransactionType.USE,transactionDto.getTransactionType());
        assertEquals(TransactionResultType.S,transactionDto.getTransactionResultType());
        assertEquals(CANCEL_AMOUNT,transactionDto.getAmount());
        assertEquals("transactionId",transactionDto.getTransactionId());
     }


    @Test
    @DisplayName("원거래 없음 - 거래 조회 실패")
    void queryTransaction_TransactionNotFound(){
        //given (parameter)


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when (method)
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.queryTransaction("transactionId"));

        //then (assertEquals)
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());

    }
}