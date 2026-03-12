package com.tl_connect.dev.modules.semester;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.modules.semester.dto.StudentSemesterDTO;
import com.tl_connect.dev.modules.student.StudentInfoService;
import com.tl_connect.dev.modules.student.dto.YearStudyDTO;


@Service
@RequiredArgsConstructor
public class SemesterService {
    
    private final SemesterRepository semesterRepository;
    private final StudentInfoService studentInfoService;
    
    public List<StudentSemesterDTO> getAllStudentSemesters(Long studentId) {
        YearStudyDTO yearStudy = studentInfoService.getYearStudy(studentId);
        
        List<Semester> semesters = semesterRepository.findAllStudentSemester(yearStudy.getStartYear(), yearStudy.getEndYear());
            return semesters.stream().map(semester -> StudentSemesterDTO.builder()
            .semesterName(semester.getSemesterName())
            .startDate(semester.getStartDate())
            .endDate(semester.getEndDate())
            .build()
        ).collect(Collectors.toList());
    }
}