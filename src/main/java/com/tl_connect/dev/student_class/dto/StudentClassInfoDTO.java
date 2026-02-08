package com.tl_connect.dev.student_class.dto;

import java.util.List;

import com.tl_connect.dev.dto.res.LecturerDTO;

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
    private LecturerDTO lecturer;
    private List<StudentInClassDTO> students;
}