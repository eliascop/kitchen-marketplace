package br.com.kitchen.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redis;

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object raw = redis.opsForValue().get(key);
        return raw != null ? (T) raw : null;
    }

    public void set(String key, Object value, Duration ttl) {
        redis.opsForValue().set(key, value, ttl);
    }
}
