package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.core.common.enums.EducationMode;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;
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
    @ImportColumn("niên khóa")
    private String cohort;
    @ImportColumn("chức vụ")
    private String position;
        @ImportColumn("hệ đào tạo")
    private EducationMode educationMode;
}
