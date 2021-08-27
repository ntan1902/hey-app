package com.hey.payment.dto.auth_system;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizeSystemResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Payload {
        String systemId;
    }
    Boolean success;
    int code;
    String message;
    Payload payload;
}
