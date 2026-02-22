package com.tl_connect.dev.modules.study_program.projection;

public interface StudyProgramHeaderView {
    String getStudyProgramName();

    Integer getStartYear();

    Integer getTotalCredits();

    String getMajorCode();

    String getMajorName();

    String getFaculty();
}
