package com.hey.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "source_id")
    Long sourceId;

    @Column(name = "target_id")
    Long targetId;

    Long amount;

    @Column(name = "created_at")
    Date createdAt;

    Integer status;

    @Column(name = "transfer_fee")
    Long transferFee;

    @Column(name = "transfer_type")
    Long transferType;
}
