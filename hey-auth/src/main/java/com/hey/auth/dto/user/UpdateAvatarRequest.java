package com.hey.auth.dto.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateAvatarRequest {
    @NotEmpty(message = "uri mustn't empty")
    private String uri;
    @NotEmpty(message = "miniUri mustn't empty")
    private String miniUri;
}
