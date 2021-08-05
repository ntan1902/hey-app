package com.hey.payment.config.rest_template;

import com.hey.payment.exception_handler.exception.UnauthorizedException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class RestTemplateErrHandler implements ResponseErrorHandler {
    private final static HttpStatus[] ERR_CODE = {HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED};

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        for (HttpStatus code : ERR_CODE) {
            if (httpResponse.getStatusCode().equals(code)) return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().value() == 400) {
            throw new UnauthorizedException("Unauthorized!");
        }
    }
}
