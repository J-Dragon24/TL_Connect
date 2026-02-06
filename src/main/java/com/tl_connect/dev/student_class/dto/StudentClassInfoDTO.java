package com.tl_connect.dev.student_class.dto;

import java.util.List;

import com.tl_connect.dev.dto.res.LecturerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentClassInfoDTO {
    private String classCode;
    private LecturerDTO lecturer;
    private List<StudentInClassDTO> students;
}