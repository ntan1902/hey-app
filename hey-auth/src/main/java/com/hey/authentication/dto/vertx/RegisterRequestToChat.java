package com.hey.authentication.dto.vertx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestToChat {
    private long userId;
    private String userName;
    private String password;
    private String fullName;
}
