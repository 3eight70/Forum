package com.hits.user.Repositories;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String key, String value, long lifetime) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, lifetime, TimeUnit.MILLISECONDS);
    }

    public Boolean checkToken(String key) {
        return redisTemplate.hasKey(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
