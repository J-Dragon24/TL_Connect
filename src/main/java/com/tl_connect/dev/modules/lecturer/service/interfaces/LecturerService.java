package com.tl_connect.dev.modules.lecturer.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.lecturer.dto.LecturerAdmInfoDTO;
import com.tl_connect.dev.modules.lecturer.dto.CreateLecturerDTO;
import com.tl_connect.dev.modules.lecturer.dto.UpdateLecturerDTO;
import com.tl_connect.dev.modules.lecturer.entity.Lecturer;
import com.tl_connect.dev.modules.lecturer.projection.LecturerRow;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface LecturerService {

    PagedResponse<LecturerAdmInfoDTO> getAllLecturers(Pageable pageable, String facultyCode);

    LecturerAdmInfoDTO getLecturerInfo(Long id);

    Long createLecturer(CreateLecturerDTO lecturerDTO);

    void updateLecturer(Long id, UpdateLecturerDTO lecturerDTO);

    void deleteLecturer(Long id);

    Lecturer findById(Long id);

    Page<LecturerRow> findAllLecturer(Pageable pageable, String facultyCode);

    boolean existsById(Long id);
}
