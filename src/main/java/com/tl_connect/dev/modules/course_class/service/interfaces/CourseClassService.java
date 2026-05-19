package com.tl_connect.dev.modules.course_class.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.course_class.dto.CourseClassBasicInfoDTO;
import com.tl_connect.dev.modules.course_class.dto.CourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.CreateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.UpdateCourseClassDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface CourseClassService {
    PagedResponse<CourseClassBasicInfoDTO> getAll(Pageable pageable, String facultyCode, String semesterCode);

    CourseClassDTO getDetailById(Long id);

    Long create(CreateCourseClassDTO dto);

    void update(Long id, UpdateCourseClassDTO dto);

    void delete(Long id);
}
