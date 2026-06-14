package com.tl_connect.dev.shared.ultility;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class CacheHelper {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public void evictAfterCommit(Runnable evictAction) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    evictAction.run();
                }
            }
        );
    }

    public <T> T getOrSet(String key, Duration ttl, TypeReference<T> type, Supplier<T> loader) {
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            
            T value = objectMapper.convertValue(cached, type);

            if (value instanceof Collection<?> collection && !collection.isEmpty()) {
                return value;
            }

            if (!(value instanceof Collection<?>)) {
                return value;
            }
        }
        
        T result = loader.get();
        try{
            redisTemplate.opsForValue().set(key, result, ttl);
        }catch (Exception e) {
            log.warn("Error when caching", e);
        }
        return result;
    }

    public <T> T getOrSet(String key, TypeReference<T> type, Supplier<T> loader) {
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            
            T value = objectMapper.convertValue(cached, type);

            if (value instanceof Collection<?> collection && !collection.isEmpty()) {
                return value;
            }

            if (!(value instanceof Collection<?>)) {
                return value;
            }
        }
        
        T result = loader.get();
        try{
            redisTemplate.opsForValue().set(key, result);
        }catch (Exception e) {
            log.warn("Error when caching", e);
        }
        return result;
    }


    public void evict(String key) {
        redisTemplate.delete(key);
    }

    public void evictByPrefix(String prefix) {
        String pattern = prefix + ":*";

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();

        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();

        try (Cursor<byte[]> cursor = connection.scan(options)) {

            List<String> keys = new ArrayList<>();

            while (cursor.hasNext()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }

            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("Error when evicting by prefix", e);
        }
    }

    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis serialization failed", e);
        }
    }

    public <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis deserialization failed", e);
        }
    }
}
