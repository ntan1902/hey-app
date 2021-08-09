package com.hey.auth.repository;

import com.hey.auth.entity.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Cacheable(value = "users", key = "#p0")
    Optional<User> findById(String id);

    @CachePut(value = "users", key = "#p0.id")
    User save(User user);

    Optional<User> findByUsername(String username);

    Boolean existsByUsernameOrEmail(String username, String email);

}
