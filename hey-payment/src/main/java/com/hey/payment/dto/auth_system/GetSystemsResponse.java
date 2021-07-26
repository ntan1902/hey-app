package com.hey.payment.dto.auth_system;

import com.hey.payment.dto.system.SystemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSystemsResponse {
    private Boolean success;
    private int code;
    private String message;
    List<SystemDTO> payload;
}
