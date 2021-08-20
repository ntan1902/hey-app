package com.hey.auth.exception.handler;


import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.system.InvalidSoftTokenException;
import com.hey.auth.exception.system.SystemIdNotFoundException;
import com.hey.auth.exception.system.SystemKeyInvalidException;
import com.hey.auth.exception.user.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Log4j2
public class ApiExceptionHandler {
    private ResponseEntity<Object> getResponse(HttpStatus code, String message) {
        // 1. Create payload containing exception details.
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .code(code.value())
                .message(message)
                .payload(null)
                .build();

        // 2. Return response entity
        return new ResponseEntity<>(apiResponse, code);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        return getResponse(code, "Unknown error");

    }

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());

    }

    @ExceptionHandler(value = {UserIdNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(UserIdNotFoundException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {SystemIdNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(SystemIdNotFoundException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {SystemKeyInvalidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(SystemKeyInvalidException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {InvalidJwtTokenException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(InvalidJwtTokenException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {PinNotMatchedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(PinNotMatchedException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {EmptyPinException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(EmptyPinException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {UsernameEmailExistedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(UsernameEmailExistedException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(BindException exception) {
        log.error("Request is not valid");
        HttpStatus code = HttpStatus.BAD_REQUEST;

        String errMessage = "";
        if (exception.getBindingResult().hasErrors()) {
            errMessage =exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        log.error(errMessage);

        return getResponse(code, errMessage);
    }

    @ExceptionHandler(value = {PasswordNotMatchedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleApiResponseException(PasswordNotMatchedException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.BAD_REQUEST;
        return getResponse(code, exception.getMessage());
    }

    @ExceptionHandler(value = {InvalidSoftTokenException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleApiResponseException(InvalidSoftTokenException exception) {
        log.error(exception.getMessage());
        HttpStatus code = HttpStatus.NOT_FOUND;
        return getResponse(code, exception.getMessage());
    }

}
