package com.example.account.exception;

import com.example.account.dto.ErrorResponse;
import com.example.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handlerAccountException(AccountException e){
        log.error("{} is occurred.",e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException is occurred.",e);

        return new ErrorResponse(
                ErrorCode.INVALID_REQUEST,
                ErrorCode.INVALID_REQUEST.getDescription()
        );
    }




    //유니크 key 가 있는데 키를 중복해서 저장하려고하거나 할때
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handlerDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("DataIntegrityViolationException is occurred.",e);

        return new ErrorResponse(
                ErrorCode.INVALID_REQUEST,
                ErrorCode.INVALID_REQUEST.getDescription()
        );
    }



    @ExceptionHandler(Exception.class)
    public ErrorResponse handlerException(Exception e){
        log.error("Exception is occurred.",e);

        return new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_REQUEST,
                ErrorCode.INTERNAL_SERVER_REQUEST.getDescription()
        );
    }
}
