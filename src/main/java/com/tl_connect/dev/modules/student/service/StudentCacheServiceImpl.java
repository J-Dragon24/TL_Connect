package com.tl_connect.dev.modules.student.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.student.service.interfaces.StudentCacheService;

@Service
public class StudentCacheServiceImpl implements StudentCacheService {

    @Override
    @CacheEvict(value = "studentInfo", key = "#studentId")
    public void evict(Long studentId) {
    }

    @Override
    @CacheEvict(value = "studentInfo", allEntries = true)
    public void evictAll() {    }

}
