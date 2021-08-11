package com.hey.auth.repository;

import com.hey.auth.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackListRepository extends CrudRepository<BlackList, String> {
//    void blacklistAccessToken(String accessToken);
//
//    boolean isExistInBlackListToken(String jwt);
//
//    List<Object> findAll();
//
//    void delete(String token);
}
