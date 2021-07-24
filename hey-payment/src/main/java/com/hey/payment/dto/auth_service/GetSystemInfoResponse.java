package com.hey.payment.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSystemInfoResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemInfo extends OwnerInfo{
        private long id;
        private String username;
        private String email;
        private String fullName;
    }
    Boolean success;
    int code;
    String message;
    SystemInfo payload;
}
