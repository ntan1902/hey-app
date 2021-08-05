package com.hey.auth.repository;

import com.hey.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Boolean existsByUsernameOrEmail(String username, String email);
}
