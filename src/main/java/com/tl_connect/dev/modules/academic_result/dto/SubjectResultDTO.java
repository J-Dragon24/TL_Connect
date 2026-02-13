package com.tl_connect.dev.modules.academic_result.dto;

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
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Double score10;
    private Double score4;
    private String letterGrade;
    private Boolean isPass;
}
