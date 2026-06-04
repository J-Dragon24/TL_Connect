package com.tl_connect.dev.modules.lecturer.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDTO;
import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDetailDTO;
import com.tl_connect.dev.modules.lecturer.dto.CreateAcademicAdvisorDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface AcademicAdvisorService {

    PagedResponse<AcademicAdvisorDTO> getAll(Pageable pageable);

    AcademicAdvisorDetailDTO getById(Long lecturerId);

    Long create(CreateAcademicAdvisorDTO dto);

    void delete(Long studentClassId);
}
