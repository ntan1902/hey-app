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
    private ResponseEntity<ApiResponse<Object>> getResponse(HttpStatus code, String message) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .code(code.value())
                .message(message)
                .build();

        // 2. Return response entity
        return new ResponseEntity<>(apiResponse, code);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<ApiResponse<Object>> handleException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        return getResponse(code, "Unauthorized!");
    }

    @ExceptionHandler(value = {HaveNoWalletException.class})
    public ResponseEntity<ApiResponse<Object>> handleNoWalletException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.NOT_FOUND;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {BalanceNotEnoughException.class})
    public ResponseEntity<ApiResponse<Object>> handleBalanceNotEnoughException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {ApiErrException.class})
    public ResponseEntity<ApiResponse<Object>> handleApiErrException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {SoftTokenAuthorizeException.class})
    public ResponseEntity<ApiResponse<Object>> handleSoftTokenAuthorizeException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {NegativeAmountException.class})
    public ResponseEntity<ApiResponse<Object>> handleNegativeAmountException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {MaxAmountException.class})
    public ResponseEntity<ApiResponse<Object>> handleMaxAmountException(Exception exception){
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }
    @ExceptionHandler(value = {MaxBalanceException.class})
    public ResponseEntity<ApiResponse<Object>> handleMaxBalanceException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {HadWalletException.class})
    public ResponseEntity<ApiResponse<Object>> handleHadWalletException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {BankInvalidException.class})
    public ResponseEntity<ApiResponse<Object>> handleBankInvalidException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.NOT_FOUND;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {DatabaseHasErr.class})
    public ResponseEntity<ApiResponse<Object>> handleDatabaseHasErr(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {WalletNotExistException.class})
    public ResponseEntity<ApiResponse<Object>> handleWalletNotExistException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.NO_CONTENT;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {WrongSourceException.class})
    public ResponseEntity<ApiResponse<Object>> handleWrongSourceException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {WrongTargetException.class})
    public ResponseEntity<ApiResponse<Object>> handleWrongTargetException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {SourceAndTargetAreTheSameException.class})
    public ResponseEntity<ApiResponse<Object>> handleSourceAndTargetAreTheSameException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }
}
