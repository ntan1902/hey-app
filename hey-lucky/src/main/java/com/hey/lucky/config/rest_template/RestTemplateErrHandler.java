package com.hey.lucky.config.rest_template;

import com.hey.lucky.exception_handler.exception.InternalServerErrException;
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
    public void handleError(ClientHttpResponse httpResponse) {
        switch (httpResponse.getStatusCode().value()){
            case 401:{
                throw new InternalServerErrException("Unauthorize!");
            }
        }
    }
}
