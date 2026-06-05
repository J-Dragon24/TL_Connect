package com.tl_connect.dev.modules.academic_result.dto;

import java.math.BigDecimal;

import com.tl_connect.dev.shared.ultility.FileProcess.annotation.ExcelColumn;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportAcademicResultDTO {
    
    @ExcelColumn(header = "Mã sinh viên")
    @NotNull(message = "Mã sinh viên không được để trống")
    private String studentCode;
    
    @ExcelColumn(header = "Mã môn học")
    @NotNull(message = "Mã môn học không được để trống")
    private String subjectCode;

    @ExcelColumn(header = "Học kỳ")
    @NotNull(message = "Học kỳ không được để trống")
    private String semesterCode;
    
    @ExcelColumn(header = "Điểm chuyên cần")
    private BigDecimal attendanceScore;

    @ExcelColumn(header = "Điểm giữa kỳ")
    private BigDecimal midtermScore;

    @ExcelColumn(header = "Điểm cuối kỳ")
    private BigDecimal finalScore;

    @ExcelColumn(header = "Điểm hệ số 10")
    private BigDecimal score10;

    @ExcelColumn(header = "Điểm hệ số 4")
    private BigDecimal score4;

    @ExcelColumn(header = "Loại")
    private String letterGrade;

    @ExcelColumn(header = "Đạt")
    private Boolean isPass;
}
