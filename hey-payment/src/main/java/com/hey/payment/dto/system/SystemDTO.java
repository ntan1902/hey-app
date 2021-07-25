package com.hey.payment.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDTO {
    private long id;
    private String systemName;
    private int numberOfWallet;
}