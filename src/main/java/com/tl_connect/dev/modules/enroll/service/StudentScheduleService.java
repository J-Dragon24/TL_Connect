package com.tl_connect.dev.modules.enroll.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.modules.enroll.cache.StudentScheduleCache;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentScheduleService {

    private final StudentCourseClassRepository studentCourseClassRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "schedule:student:";

    public StudentScheduleCache getStudentSchedule(Long studentId, Long semesterId) {
        List<ScheduleInterval> intervals = getRawFromCache(studentId, semesterId);

        StudentScheduleCache cache = new StudentScheduleCache();
        for (ScheduleInterval interval : intervals) {
            cache.addSchedule(interval.getDayOfWeek(), interval);
        }
        return cache;
    }

    private List<ScheduleInterval> getRawFromCache(Long studentId, Long semesterId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<ScheduleInterval>>() {});
        }

        List<ScheduleInterval> intervals = studentCourseClassRepository.findCurrentSchedule(studentId, semesterId);

        redisTemplate.opsForValue().set(cacheKey, intervals, 7, TimeUnit.DAYS);
        return intervals;
    }

    public void addToCache(Long studentId, Long semesterId, List<ScheduleInterval> newSchedules) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached == null) return;

        List<ScheduleInterval> intervals = objectMapper.convertValue(cached, new TypeReference<List<ScheduleInterval>>() {});
        intervals.addAll(newSchedules);

        redisTemplate.opsForValue().set(cacheKey, intervals, 7, TimeUnit.DAYS);
    }

    public void removeFromCache(Long studentId, Long semesterId, Long courseClassId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached == null) return;

        List<ScheduleInterval> intervals = objectMapper.convertValue(cached, new TypeReference<List<ScheduleInterval>>() {});
        intervals.removeIf(i -> i.getCourseClassId().equals(courseClassId));

        redisTemplate.opsForValue().set(cacheKey, intervals, 7, TimeUnit.DAYS);
    }

    public void invalidate(Long studentId, Long semesterId) {
        redisTemplate.delete(CACHE_KEY_PREFIX + studentId + ":" + semesterId);
    }


}
