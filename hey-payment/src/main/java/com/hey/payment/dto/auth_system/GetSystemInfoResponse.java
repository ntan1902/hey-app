package com.hey.payment.dto.auth_system;

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
        private String id;
        private String systemName;
//        private long numberOfWallet;
    }
    Boolean success;
    int code;
    String message;
    SystemInfo payload;
}
