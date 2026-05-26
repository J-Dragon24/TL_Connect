package com.tl_connect.dev.modules.semester.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.semester.service.interfaces.SemesterCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemesterCacheServiceImpl implements SemesterCacheService{

    private final SemesterRepository semesterRepository;

    @Cacheable(value = "studentSemesters", key = "#startYear + '-' + #endYear")
    public List<SemesterDTO> getSemestersByYear(int startYear, int endYear) {
        List<Semester> semesters = semesterRepository.findAllStudentSemester(startYear, endYear);
        return semesters.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @CacheEvict(value = "studentSemesters", key = "#startYear + '-' + #endYear")
    public void evict(int startYear, int endYear){

    }

    @CacheEvict(value = "studentSemesters", allEntries = true)
    public void evictAll(){

    }


    private SemesterDTO toDTO(Semester semester){
        return SemesterDTO.builder()
                .id(semester.getId())
                .semesterName(semester.getSemesterName())
                .semesterCode(semester.getSemesterCode())
                .academicYears(semester.getAcademicYears())
                .semesterNumber(semester.getSemesterNumber())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .isActive(semester.getIsActive())
                .build();
    }
}
