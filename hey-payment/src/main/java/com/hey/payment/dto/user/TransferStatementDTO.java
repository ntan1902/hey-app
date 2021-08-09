package com.hey.payment.dto.user;

import com.hey.payment.dto.auth_system.OwnerInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferStatementDTO {
    private OwnerInfo source;
    private OwnerInfo target;
    private String transferCode;
    private long amount;
    private LocalDateTime createdAt;
    private int status;
    private long transferFee;
    private String description;
    private String transferType;
}
