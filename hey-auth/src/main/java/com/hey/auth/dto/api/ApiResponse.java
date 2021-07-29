package com.hey.auth.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse {
    private boolean success;
    private int code;
    private String message;
    private Object payload;
}
