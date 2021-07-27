package com.hey.lucky.dto.chat_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckUserInSessionChatResponse {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Payload {
        private boolean existed;
    }
    private boolean success;
    private int code;
    private String message;
    private Payload payload;
}
