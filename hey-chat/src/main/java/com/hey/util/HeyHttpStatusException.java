package com.hey.util;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HeyHttpStatusException extends RuntimeException {
    private final int statusCode;
    private final String code;
    private final String message;

    public HeyHttpStatusException(int statusCode, String code, String payload) {
        super(HttpResponseStatus.valueOf(statusCode).reasonPhrase(), null, false, false);
        this.statusCode = statusCode;
        this.code = code;
        this.message = payload;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
