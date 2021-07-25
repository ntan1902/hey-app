package com.hey.authentication.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SoftTokenResponse {
    private String softToken;
}
