package com.hey.auth.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemLoginRequest {
    @NotEmpty(message = "systemName must not be empty")
    private String systemName;

    @NotEmpty(message = "systemKey must not be empty")
    private String systemKey;
}
