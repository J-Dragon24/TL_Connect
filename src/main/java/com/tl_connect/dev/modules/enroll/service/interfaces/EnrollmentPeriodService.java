package com.tl_connect.dev.modules.enroll.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.enroll.dto.CreateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.dto.UpdateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface EnrollmentPeriodService {
    EnrollmentPeriod getPeriod();

    PagedResponse<EnrollmentPeriod> getAllPeriods(Pageable pageable, String semesterCode);

    EnrollmentPeriod createPeriod(CreateEnrollPeriodDTO dto);

    EnrollmentPeriod updatePeriod(Long id, UpdateEnrollPeriodDTO dto);

    void deletePeriod(Long id);

    void invalidate(Long semesterId);

    EnrollmentPeriod findNearestOrCurrent();
}
