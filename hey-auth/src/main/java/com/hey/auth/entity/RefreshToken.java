package com.hey.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 2592000)
public class RefreshToken implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;

    private String id;
}