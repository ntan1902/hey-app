package com.hey.payment.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo implements OwnerInfo{
    private long id;
    private String username;
    private String email;
    private String fullName;
}
