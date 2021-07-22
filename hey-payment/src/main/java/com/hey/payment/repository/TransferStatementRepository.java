package com.hey.payment.repository;

import com.hey.payment.entity.TransferStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferStatementRepository extends JpaRepository<TransferStatement, Long> {


}
