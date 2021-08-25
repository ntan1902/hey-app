package com.hey.auth.dto.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateAvatarRequest {
    @NotEmpty(message = "Uri mustn't empty")
    private String uri;
    @NotEmpty(message = "Mini uri mustn't empty")
    private String miniUri;
}
