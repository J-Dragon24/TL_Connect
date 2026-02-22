package com.tl_connect.dev.modules.study_program.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyProgramListItemDTO {
    private Long id;
    private String studentCode;
    private String studyProgramCode;
    private String studyProgramName;
    private Boolean isPrimary;
}
