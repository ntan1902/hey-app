package com.hey.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@RedisHash(value = "soft_token", timeToLive = 30)
public class SoftToken implements Serializable {
    private String body;

    @Id
    @AccessType(AccessType.Type.PROPERTY)
    public String getId() {
        return body;
    }
    public void setId(String body){
        this.body = body;
    }
}
