package com.hey.authentication.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse {
    private boolean success;
    private int code;
    private String message;
    private Object payload;
}
