package com.tl_connect.dev.modules.student.service.interfaces;

public interface StudentCacheService {
    void evict(Long studentId);

    void evictAll();
}
