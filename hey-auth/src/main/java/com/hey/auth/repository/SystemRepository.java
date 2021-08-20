package com.hey.auth.repository;

import com.hey.auth.entity.System;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRepository extends JpaRepository<System, String> {
    Optional<System> findBySystemName(String systemName);

    @Cacheable(value = "systems", key = "#p0")
    Optional<System> findById(String id);
}
