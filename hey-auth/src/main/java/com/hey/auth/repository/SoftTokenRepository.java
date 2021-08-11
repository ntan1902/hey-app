package com.hey.auth.repository;

import com.hey.auth.entity.SoftToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftTokenRepository extends CrudRepository<SoftToken, String> {
}
