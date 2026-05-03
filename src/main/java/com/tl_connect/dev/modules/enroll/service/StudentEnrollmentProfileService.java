package com.tl_connect.dev.modules.enroll.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.academic_result.repository.StudentSemesterSummaryRepository;
import com.tl_connect.dev.modules.academic_result.repository.StudentSubjectResultRepository;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentEnrollmentProfileService {
    private final StudentSubjectResultRepository subjectResultRepository;
    private final StudentSemesterSummaryRepository summaryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "enrollment_profile:student:";

    public StudentEnrollmentProfile getProfile(Long studentId, Long studyProgramId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + studyProgramId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return (StudentEnrollmentProfile) cached;
        }

        StudentEnrollmentProfile profile = StudentEnrollmentProfile.builder()
                .passedSubjectIds(subjectResultRepository.findPassedSubjectIds(studentId))
                .cumulativeGpa(summaryRepository.calculateCumulativeGpa(studentId, studyProgramId))
                .totalCredits(summaryRepository.sumTotalCredits(studentId, studyProgramId))
                .build();

        redisTemplate.opsForValue().set(cacheKey, profile, 7, TimeUnit.DAYS);
        return profile;
    }

    public void invalidateCache(Long studentId, Long studyProgramId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + studyProgramId;
        redisTemplate.delete(cacheKey);
    }
}
