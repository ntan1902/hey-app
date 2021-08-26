package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;

    private String username;

    private String email;

    private String fullName;

    private String dob;

    private String phoneNumber;

    private String avatar;

    private String mediumAvatar;

    private String smallAvatar;
}
