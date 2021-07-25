package com.hey.payment.repository;

import com.hey.payment.entity.TransferStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferStatementRepository extends JpaRepository<TransferStatement, Long> {
    @Query("select ts from TransferStatement ts where ts.sourceId = :walletId or ts.targetId = :walletId")
    List<TransferStatement> findAllBySourceIdOrTargetId(Long walletId);
}
