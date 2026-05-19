package com.tl_connect.dev.modules.academic_result.service.interfaces;

import java.io.IOException;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.academic_result.dto.AcademicResultAdmDTO;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface AcademicResultService {

    PagedResponse<AcademicResultAdmDTO> getAllAcademicResult(Pageable pageable, String facultyCode);

    AcademicResultDTO getSubjectResult(Long studentId, String studyProgramCode);

    void exportExcel(Long studentId, String studyProgramCode, HttpServletResponse response) throws IOException;
}
