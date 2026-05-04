package com.tl_connect.dev.modules.course_class.projection;

import java.time.LocalDate;

public interface CourseClassRow {
    Long getId();
    String getLecturerCode();
    String getLecturerName();
    String getSubjectCode();
    String getSubjectName();
    String getSemesterName();
    String getSemesterCode();
    String getAcademicYears();
    Integer getSemesterNumber();
    LocalDate getStartDate();
    LocalDate getEndDate();
    String getClassCode();
    String getClassName();
    Integer getCapacity();
    Integer getEnrolledCount();
    Boolean getIsActive();
}
