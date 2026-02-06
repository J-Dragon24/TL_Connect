package com.tl_connect.dev.academic.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterSubjectsDTO {
    private Long semesterId;
    private String semesterName;
    private List<TrainingProgramSubjectDTO> subjects;
}
