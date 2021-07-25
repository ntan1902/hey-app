package com.hey.lucky.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLuckyMoneyRequest {
    private String softToken;
    private String message;
    private String sessionChatId;
    private String type;
    private int numberBag;
}
