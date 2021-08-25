package com.hey.integration.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name="transfer_statements",
        indexes = {
                @Index(name = "i_source_id", columnList = "source_id"),
                @Index(name = "i_target_id", columnList = "target_id")
        }
)
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

    private String message;

    @Column(name = "transfer_fee")
    private Long transferFee;

    @Column(name = "transfer_type")
    private String transferType;

    public TransferStatement(Long id, Long sourceId, Long targetId, Long amount, LocalDateTime createdAt, Integer status, String message, Long transferFee, String transferType) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = status;
        this.message = message;
        this.transferFee = transferFee;
        this.transferType = transferType;
    }

    public TransferStatement() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(Long transferFee) {
        this.transferFee = transferFee;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
