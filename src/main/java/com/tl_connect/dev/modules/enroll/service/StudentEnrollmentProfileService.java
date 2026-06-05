package com.tl_connect.dev.modules.enroll.service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.academic_result.service.interfaces.AcademicResultService;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.shared.ultility.CacheHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentEnrollmentProfileService {
    private final AcademicResultService academicResultService;
    private final CacheHelper cacheHelper;

    private static final String CACHE_KEY_PREFIX = "enrollment_profile:student:";

    public StudentEnrollmentProfile getProfile(Long studentId, Long studyProgramId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + studyProgramId;
        return cacheHelper.getOrSet(cacheKey, Duration.ofDays(7), new TypeReference<StudentEnrollmentProfile>() {}, () -> {
            List<StudentSubjectResult> results = academicResultService.findSubjectResultByStudentId(studentId);

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
                    .cumulativeGpa(academicResultService.calculateCumulativeGpa(studentId, studyProgramId))
                    .totalCredits(academicResultService.sumTotalCredits(studentId, studyProgramId))
                    .build();

            return profile;
        });
    }

    public void invalidateCache(Long studentId, Long studyProgramId) {
        String cacheKey = CACHE_KEY_PREFIX + studentId + ":" + studyProgramId;
        cacheHelper.evict(cacheKey);
    }
}
