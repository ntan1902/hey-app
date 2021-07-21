package com.hey.authentication.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemLoginRequest {
    private String systemName;
    private String systemKey;
}
