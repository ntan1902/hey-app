package com.hey.lucky.dto.chat_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseChat {
    private boolean success;
    private int code;
    private String message;
    private String payload;
}
