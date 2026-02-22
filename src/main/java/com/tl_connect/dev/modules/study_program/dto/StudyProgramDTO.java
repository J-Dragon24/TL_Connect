package com.tl_connect.dev.modules.study_program.dto;

import java.util.List;

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
public class StudyProgramDTO {
    private String studyProgramName;
    private Integer yearStart;
    private Integer totalCredits;
    private MajorDTO major;
    private List<SemesterSubjectsDTO> semesters;
}
