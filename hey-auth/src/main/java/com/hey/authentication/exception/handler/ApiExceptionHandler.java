package com.hey.authentication.exception.handler;


import com.hey.authentication.exception.jwt.InvalidJwtTokenException;
import com.hey.authentication.exception.system.SystemIdNotFoundException;
import com.hey.authentication.exception.system.SystemKeyInvalidException;
import com.hey.authentication.exception.user.PinNotMatchedException;
import com.hey.authentication.exception.user.UserIdNotFoundException;
import com.hey.authentication.dto.api.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;

@RestControllerAdvice
@Log4j2
public class ApiExceptionHandler {
    private ResponseEntity<Object> getResponse(HttpStatus code, String message) {
        // 1. Create payload containing exception details.
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .code(code.value())
                .message(message)
                .payload("")
                .build();

        // 2. Return response entity
        return new ResponseEntity<>(apiResponse, code);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, "Unknown error");

    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());

    }

    @ExceptionHandler(value = {UserIdNotFoundException.class})
    public ResponseEntity<Object> handleApiResponseException(UserIdNotFoundException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {SystemIdNotFoundException.class})
    public ResponseEntity<Object> handleApiResponseException(SystemIdNotFoundException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {SystemKeyInvalidException.class})
    public ResponseEntity<Object> handleApiResponseException(SystemKeyInvalidException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {InvalidJwtTokenException.class})
    public ResponseEntity<Object> handleApiResponseException(InvalidJwtTokenException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {PinNotMatchedException.class})
    public ResponseEntity<Object> handleApiResponseException(PinNotMatchedException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }
}
