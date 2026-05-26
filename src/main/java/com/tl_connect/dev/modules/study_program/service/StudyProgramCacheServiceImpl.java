package com.tl_connect.dev.modules.study_program.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramCacheService;

@Service
public class StudyProgramCacheServiceImpl implements StudyProgramCacheService {
    @Override
    @CacheEvict(value = "studyProgram", key = "#studyProgramId")
    public void evict(Long studyProgramId) {}
}
