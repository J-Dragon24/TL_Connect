package com.tl_connect.dev.modules.course_class.service.interfaces;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.dto.CourseClassBasicInfoDTO;
import com.tl_connect.dev.modules.course_class.dto.CourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.CreateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.UpdateCourseClassDTO;
import com.tl_connect.dev.modules.enroll.projection.CourseClassForEnrollRow;
import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface CourseClassService {
    PagedResponse<CourseClassBasicInfoDTO> getAll(Pageable pageable, String facultyCode, String semesterCode);

    CourseClassDTO getDetailById(Long id);

    Long create(CreateCourseClassDTO dto);

    void update(Long id, UpdateCourseClassDTO dto);

    void delete(Long id);

    List<CourseClassForEnrollRow> findCourseClassForEnrollment(Long subjectId, Long semesterId);

    CourseClass findById(Long id);

    List<DetailsForCheckEnrollRow> findDetailForEnrollmentById(Long courseClassId);

    List<Long> findIdsByStudentIdAndSemesterId(Long studentId, LocalDate now);
}
