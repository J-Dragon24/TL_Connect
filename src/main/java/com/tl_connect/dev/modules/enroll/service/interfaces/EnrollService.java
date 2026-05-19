package com.tl_connect.dev.modules.enroll.service.interfaces;

import java.util.List;

import com.tl_connect.dev.modules.enroll.dto.CourseClassForEnrollDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollViewDTO;
import com.tl_connect.dev.modules.schedule.dto.ScheduleCourseClassDTO;

public interface EnrollService {
    EnrollViewDTO getAvailableSubjects(Long studentId, String studyProgramCode);

    List<CourseClassForEnrollDTO> getAvailableCourseClasses(Long subjectId, Long semesterId);

    void enroll(Long studentId, Long courseClassId, Long studyProgramId);

    void drop(Long studentId, Long courseClassId);

    List<ScheduleCourseClassDTO> getTempSchedule(Long studentId, Long semesterId);
}
