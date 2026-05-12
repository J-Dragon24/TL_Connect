package com.tl_connect.dev.modules.academic_result.service;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SubjectResultDTO;
import com.tl_connect.dev.shared.common.ultility.importer.FileParseHelper;

@Component
public class AcademicResultExporter {
    @Autowired
    private FileParseHelper fileParseHelper;

    public void exportToExcel(AcademicResultDTO result, OutputStream outputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Kết quả học tập");

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("KẾT QUẢ HỌC TẬP - " + result.getStudyProgram());
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            sheet.createRow(1);

            for (SemesterResultDTO semester : result.getSemesterResults()) {

                Row semRow = sheet.createRow(sheet.getLastRowNum() + 1);
                semRow.createCell(0).setCellValue("HỌC KỲ: " + semester.getSemester());

                fileParseHelper.exportToSheet(
                    sheet,
                    semester.getSubjectResults(),
                    SubjectResultDTO.class,
                    workbook
                );

                sheet.createRow(sheet.getLastRowNum() + 1);
            }

            workbook.write(outputStream);
        }
    }
}
