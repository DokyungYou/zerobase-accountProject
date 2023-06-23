package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

//주로 컴포넌트나 서비스나 컨트롤러에 자주 붙는 3종 @세트들
@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    //RedisRepositoryConfig 의  redissonClient()가 주입되게됨 (이름이 같아서 자동으로?)
    private final RedissonClient redissonClient;

    public void lock(String accountNumber){
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber: {}", accountNumber);  // 디버깅용

        try{
            // waitTime: lock 을 취득하는데 기다려보는 시간
            // leaseTime: lock 이 자동해제되는 시간
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);

            //명시적으로 unlock를 해주고 있지 않기때문에 lock를 다른 녀석이 획득하려고하면 5초간 계속 실패할 것임
            if(!isLock){ //lock 획득에 실패 시
                log.error("============ Lock acquisition failed ==============");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }

        }catch (AccountException e){
           throw e;
        }catch (Exception e){
            log.error("Redis lock failed", e);
        }
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNumber: {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }


    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
