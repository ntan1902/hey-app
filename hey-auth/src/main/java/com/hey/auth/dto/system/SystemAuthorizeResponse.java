package com.hey.auth.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemAuthorizeResponse {
    private String systemId;
    private String systemName;
}
