package com.hey.payment.exception_handler.handler;

import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.exception_handler.exception.UnauthorizeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class UserExceptionHandler {
    @ExceptionHandler(value = {UnauthorizeException.class})
    public ResponseEntity<ApiResponse> handleException(Exception exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(400)
                .body(ApiResponse.builder()
                        .code(400)
                        .message("Unauthorize!")
                        .build());

    }
    @ExceptionHandler(value = {HaveNoWalletException.class})
    public ResponseEntity<ApiResponse> handleNoWalletException(Exception exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .build());

    }
}
