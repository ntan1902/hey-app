package com.hey.payment.dto.auth_system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserInfoResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo extends OwnerInfo{
        private String id;
        private String username;
        private String email;
        private String fullName;
    }
    Boolean success;
    int code;
    String message;
    UserInfo payload;
}