package com.hey.payment.exception_handler.handler;

import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.exception_handler.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class UserExceptionHandler {
        private ResponseEntity<ApiResponse> getResponse(HttpStatus code, String message) {
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .code(code.value())
                .message(message)
                .payload("")
                .build();

        // 2. Return response entity
        return new ResponseEntity<>(apiResponse, code);
    }
    @ExceptionHandler(value = {UnauthorizeException.class})
    public ResponseEntity<ApiResponse> handleException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        return getResponse(code, "Unauthorize!");
    }
    @ExceptionHandler(value = {HaveNoWalletException.class})
    public ResponseEntity<ApiResponse> handleNoWalletException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }
    @ExceptionHandler(value = {BalanceNotEnoughException.class})
    public ResponseEntity<ApiResponse> handleBalanceNotEnoughException(Exception exception){
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }
    @ExceptionHandler(value = {ApiErrException.class})
    public ResponseEntity<ApiResponse> handleApiErrException(Exception exception){
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, exception.getMessage());
    }
    @ExceptionHandler(value = {SoftTokenAuthorizeException.class})
    public ResponseEntity<ApiResponse> handleSoftTokenAuthorizeException(Exception exception){
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        return getResponse(code, exception.getMessage());
    }
}
