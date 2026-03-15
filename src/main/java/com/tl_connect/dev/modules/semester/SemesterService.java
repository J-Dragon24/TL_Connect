package com.tl_connect.dev.modules.semester;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.modules.student.dto.YearStudyDTO;
import com.tl_connect.dev.modules.student.service.StudentService;


@Service
@RequiredArgsConstructor
public class SemesterService {
    
    private final SemesterRepository semesterRepository;
    private final StudentService studentInfoService;
    
    public List<Semester> getAllStudentSemesters(Long studentId) {
        YearStudyDTO yearStudy = studentInfoService.getYearStudy(studentId);
        
        List<Semester> semesters = semesterRepository.findAllStudentSemester(yearStudy.getStartYear(), yearStudy.getEndYear());
            return semesters;
    }
}