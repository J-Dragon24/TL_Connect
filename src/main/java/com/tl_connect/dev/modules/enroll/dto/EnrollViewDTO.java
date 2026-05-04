package com.tl_connect.dev.modules.enroll.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollViewDTO {
    private Long studyProgramId;
    private String studyProgramCode;
    private String studyProgramName;
    private Long semesterId;
    private List<SubjectForEnrollDTO> subjects;
}
