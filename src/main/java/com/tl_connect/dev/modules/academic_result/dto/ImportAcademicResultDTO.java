package com.tl_connect.dev.modules.academic_result.dto;

import java.math.BigDecimal;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;

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
    
    @ImportColumn("Mã sinh viên")
    @NotNull(message = "Mã sinh viên không được để trống")
    private String studentCode;
    
    @ImportColumn("Mã môn học")
    @NotNull(message = "Mã môn học không được để trống")
    private String subjectCode;

    @ImportColumn("Học kỳ")
    @NotNull(message = "Học kỳ không được để trống")
    private String semesterCode;
    
    @ImportColumn("Điểm chuyên cần")
    private BigDecimal attendanceScore;

    @ImportColumn("Điểm giữa kỳ")
    private BigDecimal midtermScore;

    @ImportColumn("Điểm cuối kỳ")
    private BigDecimal finalScore;

    @ImportColumn("Điểm hệ số 10")
    private BigDecimal score10;

    @ImportColumn("Điểm hệ số 4")
    private BigDecimal score4;

    @ImportColumn("Loại")
    private String letterGrade;

    @ImportColumn("Đạt")
    private Boolean isPass;
}
