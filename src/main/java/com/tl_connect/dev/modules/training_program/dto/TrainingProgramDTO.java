package com.tl_connect.dev.modules.training_program.dto;

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
public class TrainingProgramDTO {
    private String trainingProgramName;
    private Integer yearStart;
    private Integer totalCredits;
    private MajorDTO major;
    private List<SemesterSubjectsDTO> semesters;
}
