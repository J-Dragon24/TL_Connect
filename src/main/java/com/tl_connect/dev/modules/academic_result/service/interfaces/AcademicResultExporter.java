package com.tl_connect.dev.modules.academic_result.service.interfaces;

import java.io.IOException;

import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;

import jakarta.servlet.http.HttpServletResponse;

public interface AcademicResultExporter {
    void exportToExcel(AcademicResultDTO result, HttpServletResponse response) throws IOException;
}
