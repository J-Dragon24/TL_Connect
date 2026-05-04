package com.tl_connect.dev.modules.enroll.dto;

import java.math.BigDecimal;

import com.tl_connect.dev.modules.enroll.projection.SubjectForEnrollRow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectForEnrollDTO {
    private Long id;
    private String facultyName;
    private String facultyCode;
    private String departmentName;
    private String departmentCode;
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private Boolean isRequired;
    private String electiveGroup;
    private BigDecimal coefficient;
    private Integer lectureHours;
    private Integer practiceHours;

    public static SubjectForEnrollDTO from(SubjectForEnrollRow row) {
        SubjectForEnrollDTO dto = new SubjectForEnrollDTO();
        dto.setId(row.getSubjectId());
        dto.setFacultyName(row.getFacultyName());
        dto.setFacultyCode(row.getFacultyCode());
        dto.setDepartmentName(row.getDepartmentName());
        dto.setDepartmentCode(row.getDepartmentCode());
        dto.setSubjectCode(row.getSubjectCode());
        dto.setSubjectName(row.getSubjectName());
        dto.setCredits(row.getCredits());
        dto.setIsRequired(row.getIsRequired());
        dto.setElectiveGroup(row.getElectiveGroup());
        dto.setCoefficient(row.getCoefficient());
        dto.setLectureHours(row.getLectureHours());
        dto.setPracticeHours(row.getPracticeHours());
        return dto;
    }
}
