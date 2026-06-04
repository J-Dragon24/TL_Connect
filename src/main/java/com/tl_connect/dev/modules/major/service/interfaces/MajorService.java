package com.tl_connect.dev.modules.major.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.major.dto.CreateMajorDTO;
import com.tl_connect.dev.modules.major.dto.MajorAdmDTO;
import com.tl_connect.dev.modules.major.dto.UpdateMajorDTO;
import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface MajorService {
    PagedResponse<MajorAdmDTO> getAllMajors(Pageable pageable, String facultyCode);

    public Long createMajor(CreateMajorDTO majorDTO);

    public void updateMajor(Long id, UpdateMajorDTO majorDTO);

    public void deleteMajor(Long id);

    Major findByMajorCode(String majorCode);

    StudentMajor findPrimaryByStudentId(Long studentId);
}
