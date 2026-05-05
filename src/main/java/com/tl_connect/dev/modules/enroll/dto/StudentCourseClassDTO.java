package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalDateTime;

import com.tl_connect.dev.modules.enroll.projection.StudentCourseClassRow;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCourseClassDTO {
    private Long id;
    private String studentCode;
    private String studentName;
    private String classCode;
    private String className;
    private String subjectCode;
    private String subjectName;
    private String semesterCode;
    private String semesterName;
    private StudentCourseClassStatus status;
    private Boolean isRetake;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentCourseClassDTO from(StudentCourseClassRow row) {
        return StudentCourseClassDTO.builder()
                .id(row.getId())
                .studentCode(row.getStudentCode())
                .studentName(row.getStudentName())
                .classCode(row.getClassCode())
                .className(row.getClassName())
                .subjectCode(row.getSubjectCode())
                .subjectName(row.getSubjectName())
                .semesterCode(row.getSemesterCode())
                .semesterName(row.getSemesterName())
                .status(row.getStatus())
                .isRetake(row.getIsRetake())
                .createdAt(row.getCreatedAt())
                .updatedAt(row.getUpdatedAt())
                .build();
    }
}
