package com.tl_connect.dev.modules.academic_result.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.service.interfaces.AcademicResultExporter;
import com.tl_connect.dev.shared.ultility.FileProcess.FileParseHelper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AcademicResultExporterImpl extends FileParseHelper implements AcademicResultExporter {

    @Override
    public void exportToExcel(AcademicResultDTO result, HttpServletResponse response) throws IOException {
        newExcel();

        response = initResponseForExportExcel(response, "Ket_Qua_Hoc_Tap");
        ServletOutputStream outputStream = response.getOutputStream();

        List<SubjectResultDTO> listSubject = new ArrayList<>();
        for (SemesterResultDTO semester : result.getSemesterResults()) {
            listSubject.addAll(semester.getSubjectResults());
        }
        

        exportToSheet(
            "Kết quả học tập",
            "KẾT QUẢ HỌC TẬP - " + result.getStudyProgram(),
            listSubject,
            SubjectResultDTO.class
        );

        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
