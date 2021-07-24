package com.hey.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="transfer_statements")
public class TransferStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "target_id")
    private Long targetId;

    private Long amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Integer status;

    @Column(name = "transfer_fee")
    private Long transferFee;

    @Column(name = "transfer_type")
    private String transferType;
}
