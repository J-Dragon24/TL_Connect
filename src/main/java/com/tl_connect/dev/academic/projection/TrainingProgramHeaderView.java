package com.tl_connect.dev.academic.projection;


public interface TrainingProgramHeaderView {
    Long getId();
    String getTrainingProgramName();
    Integer getYearStart();
    Integer getTotalCredits();
    String getMajorCode();
    String getMajorName();
    String getFaculty();
}
