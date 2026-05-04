package com.tl_connect.dev.modules.enroll.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
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

        List<StudentSubjectResult> results = subjectResultRepository.findAllByStudentId(studentId);

        Set<Long> passedSubjectIds = new HashSet<>();
        Set<Long> failedSubjectIds = new HashSet<>();

        for (StudentSubjectResult result : results) {
            if (Boolean.TRUE.equals(result.getIsPass())) {
                passedSubjectIds.add(result.getSubjectId());
            } else {
                failedSubjectIds.add(result.getSubjectId());
            }
        }

        failedSubjectIds.removeAll(passedSubjectIds);

        StudentEnrollmentProfile profile = StudentEnrollmentProfile.builder()
                .passedSubjectIds(passedSubjectIds)
                .failedSubjectIds(failedSubjectIds)
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
