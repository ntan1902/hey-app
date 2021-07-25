package com.hey.lucky.exception_handler.handler;

import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public ResponseEntity<ApiResponse> handleCannotTransferMoneyException(Exception exception){
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, exception.getMessage());
    }
}
