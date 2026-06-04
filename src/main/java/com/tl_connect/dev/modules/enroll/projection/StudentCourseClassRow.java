package com.tl_connect.dev.modules.enroll.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;

public interface StudentCourseClassRow {
    Long getId();
    String getStudentCode();
    String getStudentName();
    String getClassCode();
    String getClassName();
    String getSubjectCode();
    String getSubjectName();
    String getSemesterCode();
    String getSemesterName();
    StudentCourseClassStatus getStatus();
    Boolean getIsRetake();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
