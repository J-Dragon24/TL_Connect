package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.shared.ultility.FileProcess.annotation.ExcelColumn;

import jakarta.validation.constraints.NotBlank;
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
public class AcademicInfoDTO {
    @NotBlank(message = "Niên khóa không được để trống")
    @ExcelColumn(header = "Niên khóa")
    private String cohort;

    @ExcelColumn(header = "Chức vụ")
    private String position;
}
