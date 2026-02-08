package com.tl_connect.dev.academic.projection;

public interface TrainingProgramSubjectRow {
    Long getSemesterId();

    String getSemesterName();

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
