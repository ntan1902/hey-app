package com.hey.lucky.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReceivedInfo {
    private String fullName;
    private long amount;
    private String receivedAt;
}
