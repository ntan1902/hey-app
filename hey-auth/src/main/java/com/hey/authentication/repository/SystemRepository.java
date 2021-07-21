package com.hey.authentication.repository;

import com.hey.authentication.entity.System;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRepository extends JpaRepository<System, Long> {
    Optional<System> findBySystemName(String systemName);
}
