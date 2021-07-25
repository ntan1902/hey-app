package com.hey.lucky.dto.payment_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransferFromUserResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Payload {
        private long amount;
    }
    Boolean success;
    int code;
    String message;
    Payload payload;

}
