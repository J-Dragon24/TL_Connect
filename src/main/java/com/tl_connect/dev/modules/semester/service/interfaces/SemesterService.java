package com.tl_connect.dev.modules.semester.service.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.dto.CreateSemesterDTO;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.semester.dto.UpdateSemesterDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface SemesterService {
     List<SemesterDTO> getAllStudentSemesters(Long studentId);

     PagedResponse<SemesterDTO> getAll(Pageable pageable);

     Long createSemester(CreateSemesterDTO dto);

     void updateSemester(Long id, UpdateSemesterDTO dto);

     void deleteSemester(Long id);

     List<Semester> findBySemesterCodeIn(Set<String> semesterCodes);

     Semester findByIdAndIsActive(Long id);

     boolean existsById(Long id);

     Semester findBySemesterCode(String semesterCode);

     Semester findByDate(LocalDate date);
}
