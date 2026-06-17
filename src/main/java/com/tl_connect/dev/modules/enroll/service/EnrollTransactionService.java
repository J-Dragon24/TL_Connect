package com.tl_connect.dev.modules.enroll.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;
import com.tl_connect.dev.modules.enroll.dto.EnrollmentValidationResult;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;

import lombok.RequiredArgsConstructor;

/**
 * Chứa write transaction của luồng enroll trong một bean riêng.
 *
 * <p>Tách ra khỏi {@link EnrollServiceImpl} để Spring AOP proxy hoạt động
 * đúng với {@code @Transactional} (tránh self-invocation problem).
 *
 * <p>Transaction này được thiết kế ngắn nhất có thể, chỉ gồm 2 bước:
 * <ol>
 *   <li>Atomic SQL: {@code UPDATE course_classes SET enrolled_count = enrolled_count + 1
 *       WHERE id = :id AND enrolled_count < capacity}</li>
 *   <li>INSERT / UPDATE student_course_class</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class EnrollTransactionService {

    private final CourseClassService courseClassService;
    private final StudentCourseClassService studentCourseClassService;

    /**
     * Thực thi write transaction ngắn. Nhận kết quả đã validate từ
     * {@link EnrollmentPreCheckService}, không phát sinh thêm read query nào.
     *
     * @return {@link StudentCourseClass} đã được lưu (để lấy semesterId cho event).
     * @throws ErrorException nếu lớp đã đầy hoặc vi phạm unique constraint.
     */
    @Transactional
    public StudentCourseClass execute(
            Long studentId,
            Long courseClassId,
            EnrollmentValidationResult validation) {

        // 1. Atomic check + update: không race condition
        int updatedRows = courseClassService.increaseEnrolledCount(courseClassId);
        if (updatedRows == 0) {
            throw new ErrorException(ResponseStatus.CLASS_FULL, "Course class is full");
        }

        // 2. Upsert StudentCourseClass
        StudentCourseClass scc = validation.getExistingScc();
        if (scc == null) {
            scc = new StudentCourseClass();
            scc.setStudentId(studentId);
            scc.setCourseClassId(courseClassId);
            scc.setSubjectId(validation.getSubjectId());
            scc.setSemesterId(validation.getSemesterId());
        }
        scc.setStatus(StudentCourseClassStatus.PENDING);
        scc.setIsRetake(validation.isRetake());

        try {
            return studentCourseClassService.save(scc);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Already enrolled");
        }
    }
}
