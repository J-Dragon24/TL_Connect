package com.tl_connect.dev.modules.enroll;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollService {
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final CourseClassLogRepository courseClassLogRepository;
}
