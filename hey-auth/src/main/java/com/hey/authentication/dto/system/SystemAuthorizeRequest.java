package com.hey.authentication.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemAuthorizeRequest {
    private String jwtSystem;
}
