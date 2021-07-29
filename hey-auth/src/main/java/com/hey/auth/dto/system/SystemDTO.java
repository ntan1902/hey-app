package com.hey.auth.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDTO {
    private String id;
    private String systemName;
    private long numberOfWallet;
}
