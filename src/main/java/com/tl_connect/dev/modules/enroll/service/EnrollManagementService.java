package com.tl_connect.dev.modules.enroll.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassFilter;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.projection.StudentCourseClassRow;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollManagementService {
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final StudentScheduleService studentScheduleService;

    @Transactional
    public void confirm(Long semesterId) {
        studentCourseClassRepository.updateStatusBySemesterId(semesterId, StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED);
    }

    public PagedResponse<StudentCourseClassDTO> getAllStudentEnrollment(StudentCourseClassFilter filter, Pageable pageable) {
        Page<StudentCourseClassRow> rows = studentCourseClassRepository.getAllStudentEnrollment(filter.getSemesterId(),filter.getStudentId(), filter.getFacultyId(), pageable);
        return new PagedResponse<>(
            rows.getContent().stream().map(StudentCourseClassDTO::from).toList(),
            rows.getNumber(),
            rows.getSize(),
            rows.getTotalElements(),
            rows.getTotalPages(),
            rows.isFirst(),
            rows.isLast());
    }

    @Transactional
    public void cancel(Long id) {
        StudentCourseClass studentCourseClass = studentCourseClassRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("StudentCourseClass not found"));
        if(studentCourseClass.getStatus() != StudentCourseClassStatus.PENDING) {
            throw new ConflictException("StudentCourseClass is not in PENDING state");
        }
        studentCourseClass.setStatus(StudentCourseClassStatus.REJECTED);
        studentCourseClassRepository.save(studentCourseClass);
        
        studentScheduleService.removeFromCache(studentCourseClass.getStudentId(), studentCourseClass.getSemesterId(), studentCourseClass.getCourseClassId());
    }
}
