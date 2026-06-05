package com.tl_connect.dev.modules.academic_result.dto;

import java.math.BigDecimal;

import com.tl_connect.dev.shared.ultility.FileProcess.annotation.ExcelColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResultDTO {
    private Long id;
    
    @ExcelColumn(header = "Mã môn học", order = 0)
    private String subjectCode;

    @ExcelColumn(header = "Tên môn học", order = 1)
    private String subjectName;

    @ExcelColumn(header = "Số tín chỉ", order = 2)
    private Integer credits;
    private BigDecimal attendanceScore;
    private BigDecimal midtermScore;
    private BigDecimal finalScore;
    
    @ExcelColumn(header = "Điểm hệ 10", order = 3)
    private BigDecimal score10;

    @ExcelColumn(header = "Điểm hệ 4", order = 4)
    private BigDecimal score4;

    @ExcelColumn(header = "Điểm chữ", order = 5)
    private String letterGrade;
    
    private Boolean isPass;
}
