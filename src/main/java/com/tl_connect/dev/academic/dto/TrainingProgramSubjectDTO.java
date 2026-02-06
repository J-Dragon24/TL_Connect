package com.tl_connect.dev.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgramSubjectDTO {
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Boolean isRequired;
    private String electiveGroup;
    private Integer lectureHours;
    private Integer practiceHours;
    private String faculty;
    private String department;
}
