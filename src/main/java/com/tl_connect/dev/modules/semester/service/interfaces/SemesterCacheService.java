package com.tl_connect.dev.modules.semester.service.interfaces;

import java.util.List;

import com.tl_connect.dev.modules.semester.dto.SemesterDTO;

public interface SemesterCacheService {
    List<SemesterDTO> getSemestersByYear(int startYear, int endYear);

    void evict(int startYear, int endYear);

    void evictAll();
}
