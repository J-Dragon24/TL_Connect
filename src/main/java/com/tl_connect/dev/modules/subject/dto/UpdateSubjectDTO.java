package com.tl_connect.dev.modules.subject.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
public class UpdateSubjectDTO implements StudyDTOInterface {
    private Long facultyId;
    private Long departmentId;
    @Size(min = 3, max = 10, message = "Subject code must be between 3 and 10 characters")
    private String subjectCode;
    @Size(min = 3, max = 100, message = "Subject name must be between 3 and 100 characters")
    private String subjectName;
    @Min(value = 1, message = "Credits must be >= 1")
    private Integer credits;
    @Min(value = 1, message = "Coefficient must be >= 1")
    private BigDecimal coefficient;
    @Min(value = 1, message = "Lecture hours must be >= 1")
    private Integer lectureHours;
    @Min(value = 1, message = "Practice hours must be >= 1")
    private Integer practiceHours;
    private List<SubjectPrerequisiteGroupDTO> prerequisiteGroups;
    private List<EnrollmentConditionDTO> enrollmentConditions;
}
