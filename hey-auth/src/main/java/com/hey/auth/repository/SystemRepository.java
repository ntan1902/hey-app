package com.hey.auth.repository;

import com.hey.auth.entity.System;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRepository extends JpaRepository<System, String> {
    Optional<System> findBySystemName(String systemName);
}
