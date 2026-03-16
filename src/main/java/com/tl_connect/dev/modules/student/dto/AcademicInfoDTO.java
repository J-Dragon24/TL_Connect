package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;

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
    @ImportColumn("Niên khóa")
    private String cohort;

    @ImportColumn("Chức vụ")
    private String position;
}
