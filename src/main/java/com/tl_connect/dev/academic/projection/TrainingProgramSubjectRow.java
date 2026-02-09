package com.tl_connect.dev.academic.projection;

import java.time.LocalDate;

public interface TrainingProgramSubjectRow {
    Long getSemesterId();

    String getSemesterName();

    LocalDate getSemesterStartDate();

    LocalDate getSemesterEndDate();

    Long getSubjectId();

    String getSubjectCode();

    String getSubjectName();

    Integer getCredits();

    Boolean getIsRequired();

    String getElectiveGroup();

    Integer getLectureHours();

    Integer getPracticeHours();

    String getFaculty();

    String getDepartment();
}
