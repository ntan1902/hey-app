package com.hey.payment.repository;

import com.hey.payment.entity.TransferStatement;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferStatementRepository extends JpaRepository<TransferStatement, Long> {
    @Query(value =
            "SELECT * FROM transfer_statements ts " +
                    "WHERE ts.source_id = :walletId " +
                    "OR ts.target_id = :walletId " +
                    "ORDER BY ts.created_at DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    @Cacheable(value = "transfer_statements", key = "{#p0 + ':' + #p1 + ':' + #p2}")
    List<TransferStatement> findAllBySourceIdOrTargetIdOrderByCreatedAtDesc(Long walletId, int offset, int limit);

    @CacheEvict(value = "transfer_statements", allEntries = true)
    TransferStatement save(TransferStatement transferStatement);

}
