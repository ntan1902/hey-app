package com.hey.auth.dto.vertx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestToChat {
    private String userId;
    private String userName;
    private String password;
    private String fullName;
}
