package com.hey.payment.repository;

import com.hey.payment.entity.TransferStatement;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferStatementRepository extends JpaRepository<TransferStatement, Long> {
    @Query("select ts from TransferStatement ts where ts.sourceId = :walletId or ts.targetId = :walletId")
    @Cacheable(value = "transfer_statements", key = "{#p0 + ':' + #p1.pageNumber + ':' + #p1.pageSize}")
    List<TransferStatement> findAllBySourceIdOrTargetId(Long walletId, Pageable pageable);

    @CacheEvict(value = "transfer_statements", allEntries = true)
    TransferStatement save(TransferStatement transferStatement);

    @Query("select ts from TransferStatement ts where (ts.sourceId = :walletId or ts.targetId = :walletId) and ts.createdAt < :createdAtDT ")
    List<TransferStatement> findAllBySourceIdOrTargetIdAndCreatedBefore(Long walletId, LocalDateTime createdAtDT, Pageable pageable);
}
