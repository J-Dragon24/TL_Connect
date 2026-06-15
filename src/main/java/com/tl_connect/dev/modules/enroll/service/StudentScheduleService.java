package com.tl_connect.dev.modules.enroll.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tl_connect.dev.modules.enroll.cache.StudentScheduleCache;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;
import com.tl_connect.dev.shared.ultility.CacheHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentScheduleService {

    private final StudentCourseClassService studentCourseClassService;
    private final CacheHelper cacheHelper;

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

        return cacheHelper.getOrSet(cacheKey, Duration.ofDays(7), new TypeReference<List<ScheduleInterval>>() {}, () -> {
            List<ScheduleInterval> intervals = studentCourseClassService.findCurrentSchedule(studentId, semesterId);
            return intervals;
        });

    }

    public void addToCache(Long studentId, Long semesterId, List<ScheduleInterval> newSchedules) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        List<ScheduleInterval> intervals = cacheHelper.getOrSet(
            cacheKey, 
            Duration.ofDays(7),
            new TypeReference<List<ScheduleInterval>>() {},
            () -> studentCourseClassService.findCurrentSchedule(studentId, semesterId)
        );
        
        intervals.addAll(newSchedules);

        cacheHelper.set(cacheKey, intervals, Duration.ofDays(7));
    }

    public void removeFromCache(Long studentId, Long semesterId, Long courseClassId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + semesterId;

        List<ScheduleInterval> intervals = cacheHelper.getOrSet(
            cacheKey, 
            Duration.ofDays(7),
            new TypeReference<List<ScheduleInterval>>() {},
            () -> studentCourseClassService.findCurrentSchedule(studentId, semesterId)
        );
        
        intervals.removeIf(i -> i.getCourseClassId().equals(courseClassId));

        cacheHelper.set(cacheKey, intervals, Duration.ofDays(7));
    }

    public void invalidate(Long studentId, Long semesterId) {
        cacheHelper.evict(CACHE_KEY_PREFIX + studentId + ":" + semesterId);
    }

    public void invalidateAll(Long semesterId) {
        cacheHelper.evictByPattern(CACHE_KEY_PREFIX + "*" + ":" + semesterId);
    }


}
