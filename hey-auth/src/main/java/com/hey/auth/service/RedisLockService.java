package com.hey.auth.service;

public interface RedisLockService {
    void lock(String lockKey);

    boolean tryLock(String lockKey);

    boolean tryLock(String lockKey, long seconds);

    void unlock(String lockKey);
}
