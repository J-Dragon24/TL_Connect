package com.tl_connect.dev.modules.student_class.dto;

import java.util.List;

import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;

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
public class StudentClassInfoDTO {
    private String classCode;
    private String majorName;
    private Integer startYear;
    private LecturerDTO academicAdvisor;
    private List<StudentInClassDTO> students;
}