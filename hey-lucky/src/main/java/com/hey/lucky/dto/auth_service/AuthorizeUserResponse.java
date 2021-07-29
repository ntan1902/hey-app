package com.hey.lucky.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizeUserResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Payload {
        String userId;
    }
    Boolean success;
    int code;
    String message;
    Payload payload;
}
