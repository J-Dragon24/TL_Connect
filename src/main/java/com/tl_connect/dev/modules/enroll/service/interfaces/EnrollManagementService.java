package com.tl_connect.dev.modules.enroll.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassFilter;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface EnrollManagementService {
    void confirm(Long semesterId);

    PagedResponse<StudentCourseClassDTO> getAllStudentEnrollment(StudentCourseClassFilter filter, Pageable pageable);
    
    void cancel(Long id);
}
