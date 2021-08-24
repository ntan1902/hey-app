package com.hey.integration.repository;

import com.hey.integration.entity.TransferStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransferStatementRepository extends JpaRepository<TransferStatement, Long> {
}
