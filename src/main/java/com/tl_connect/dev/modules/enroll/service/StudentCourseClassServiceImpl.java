package com.tl_connect.dev.modules.enroll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseClassServiceImpl implements StudentCourseClassService {
    
    private final StudentCourseClassRepository studentCourseClassRepository;

    public List<ScheduleInterval> findCurrentSchedule(Long studentId, Long semesterId){
        return studentCourseClassRepository.findCurrentSchedule(studentId, semesterId);
    }
    
}
