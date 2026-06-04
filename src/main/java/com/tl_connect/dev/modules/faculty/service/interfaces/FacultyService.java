package com.tl_connect.dev.modules.faculty.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.dto.CreateFacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.FacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.UpdateFacultyDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface FacultyService {
    PagedResponse<FacultyDTO> getAllFaculties(Pageable pageable);

    Long createFaculty(CreateFacultyDTO facultyDTO);

    void updateFaculty(Long id, UpdateFacultyDTO facultyDTO);

    void deleteFaculty(Long id);

    Faculty findByIdAndIsActive(Long id);

    boolean existsById(Long id);
}
