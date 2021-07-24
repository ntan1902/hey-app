package com.hey.payment.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T payload;
}
