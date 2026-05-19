package com.tl_connect.dev.modules.enroll.service.interfaces;

import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;

public interface EnrollmentConditionService {
    void check(Long subjectId, StudentEnrollmentProfile profile);
}
