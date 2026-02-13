package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.core.common.enums.EducationMode;

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
    private String cohort;
    private String position;
    private EducationMode educationMode;
}
