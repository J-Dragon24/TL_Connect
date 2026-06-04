package com.tl_connect.dev.modules.study_program.dto;

import java.io.Serializable;
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
public class StudyProgramDTO implements Serializable{
    private String studyProgramName;
    private String studyProgramCode;
    private Integer yearStart;
    private Integer totalCredits;
    private MajorDTO major;
    private List<SemesterSubjectsDTO> semesters;
}
