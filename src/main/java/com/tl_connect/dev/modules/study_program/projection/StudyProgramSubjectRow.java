package com.tl_connect.dev.modules.study_program.projection;

import java.time.LocalDate;

public interface StudyProgramSubjectRow {
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
