package com.tl_connect.dev.modules.study_program.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.List;

import com.tl_connect.dev.modules.subject.dto.SubjectPrerequisiteGroupDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyProgramSubjectDTO {
    private Long id;
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Boolean isRequired;
    private String electiveGroup;
    private Integer lectureHours;
    private Integer practiceHours;
    private List<SubjectPrerequisiteGroupDTO> subjectPrerequisite;
    private String faculty;
    private String department;
}
