package com.tl_connect.dev.modules.enroll.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.repository.EnrollmentPeriodRepository;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentPeriodService {

    private final EnrollmentPeriodRepository enrollmentPeriodRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "enrollment:period:";

    public EnrollmentPeriod getPeriod(Long semesterId) {
        String cacheKey = CACHE_KEY_PREFIX + semesterId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return (EnrollmentPeriod) cached;

        EnrollmentPeriod period = enrollmentPeriodRepository.findBySemesterId(semesterId)
            .orElseThrow(() -> new NotFoundException("Enrollment period not found"));

        long secondsUntilEnd = ChronoUnit.SECONDS.between(
            LocalDateTime.now(), period.getEndTime()
        );
        redisTemplate.opsForValue().set(cacheKey, period, secondsUntilEnd, TimeUnit.SECONDS);

        return period;
    }

    public void invalidate(Long semesterId) {
        redisTemplate.delete(CACHE_KEY_PREFIX + semesterId);
    }
}
