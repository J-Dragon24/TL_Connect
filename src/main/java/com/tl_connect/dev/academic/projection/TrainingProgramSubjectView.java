package com.tl_connect.dev.academic.projection;

public interface TrainingProgramSubjectView {
    Long getSemesterId();

    String getSemesterName();

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
