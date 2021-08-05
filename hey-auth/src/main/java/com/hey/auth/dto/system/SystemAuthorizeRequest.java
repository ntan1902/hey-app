package com.hey.auth.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemAuthorizeRequest {
    @NotEmpty(message = "jwtSystem must not be empty")
    private String jwtSystem;
}
