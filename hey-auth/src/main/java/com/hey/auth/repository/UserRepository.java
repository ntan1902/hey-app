package com.hey.auth.repository;

import com.hey.auth.entity.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Cacheable(value = "users", key = "#p0")
    Optional<User> findById(String id);

    @CachePut(value = "users", key = "#p0.id")
    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsernameOrEmail(String username, String email);

    @Query("select u from User u where u.fullName like %:key% or u.email like %:key%")
    List<User> findAllByFullNameContainsOrEmailContains(String key);

}
