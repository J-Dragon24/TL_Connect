package com.tl_connect.dev.modules.enroll.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tl_connect.dev.modules.enroll.cache.StudentScheduleCache;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.shared.common.ultility.JsonHelper;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentScheduleService {

    private final StudentCourseClassService studentCourseClassService;
    private final StringRedisTemplate redisTemplate;
    private final JsonHelper jsonHelper;

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

        String cached = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return jsonHelper.fromJson(cached, new TypeReference<List<ScheduleInterval>>() {});
        }

        List<ScheduleInterval> intervals = studentCourseClassService.findCurrentSchedule(studentId, semesterId);

        try {
            redisTemplate.opsForValue().set(cacheKey, jsonHelper.toJson(intervals), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Failed to write schedule to cache", e);
        }
        
        return intervals;
    }

    public void addToCache(Long studentId, Long semesterId, List<ScheduleInterval> newSchedules) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        String cached = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cached == null) return;

        List<ScheduleInterval> intervals = jsonHelper.fromJson(cached, new TypeReference<List<ScheduleInterval>>() {});
        intervals.addAll(newSchedules);

        try {
            redisTemplate.opsForValue().set(cacheKey, jsonHelper.toJson(intervals), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Failed to write schedule to cache", e);
        }
    }

    public void removeFromCache(Long studentId, Long semesterId, Long courseClassId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        String cached = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cached == null) return;

        List<ScheduleInterval> intervals = jsonHelper.fromJson(cached, new TypeReference<List<ScheduleInterval>>() {});
        intervals.removeIf(i -> i.getCourseClassId().equals(courseClassId));

        try {
            redisTemplate.opsForValue().set(cacheKey, jsonHelper.toJson(intervals), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Failed to write schedule to cache", e);
        }
    }

    public void invalidate(Long studentId, Long semesterId) {
        redisTemplate.delete(CACHE_KEY_PREFIX + studentId + ":" + semesterId);
    }

    public void invalidateAll(Long semesterId) {
        String pattern = CACHE_KEY_PREFIX + "*" + ":" + semesterId;
        redisTemplate.delete(redisTemplate.keys(pattern));
    }


}
