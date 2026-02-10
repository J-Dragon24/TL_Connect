package com.tl_connect.dev.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Boolean isRequired;
    private String electiveGroup;
    private Integer lectureHours;
    private Integer practiceHours;
    private List<SubjectPrerequisiteDTO> subjectPrerequisite;
    private String faculty;
    private String department;
}
