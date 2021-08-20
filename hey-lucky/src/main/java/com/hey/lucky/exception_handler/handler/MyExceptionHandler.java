package com.hey.lucky.exception_handler.handler;

import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.exception_handler.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class MyExceptionHandler {
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

    @ExceptionHandler(value = {CannotTransferMoneyException.class})
    public ResponseEntity<ApiResponse> handleCannotTransferMoneyException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {InvalidLuckyMoneyException.class})
    public ResponseEntity<ApiResponse> handleLuckyMoneyInvalidException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {LuckyMoneyExpiredException.class})
    public ResponseEntity<ApiResponse> handleLuckyMoneyExpiredException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {OutOfBagException.class})
    public ResponseEntity<ApiResponse> handleOutOfBagException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.NOT_ACCEPTABLE;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {CannotGetUserInfo.class})
    public ResponseEntity<ApiResponse> handleCannotGetUserInfo(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.NOT_ACCEPTABLE;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {ErrCallApiException.class})
    public ResponseEntity<ApiResponse> handleErrCallApiException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {ErrCallChatApiException.class})
    public ResponseEntity<ApiResponse> handleErrCallChatApiException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.CREATED;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {HadReceivedException.class})
    public ResponseEntity<ApiResponse> handleHadReceivedException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {UnauthorizeException.class})
    public ResponseEntity<ApiResponse> handleUnauthorizeException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {UserNotInSessionChatException.class})
    public ResponseEntity<ApiResponse> handleUserNotInSessionChatException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

}
