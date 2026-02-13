package com.tl_connect.dev.modules.training_program.projection;


public interface TrainingProgramHeaderView {
    Long getId();
    String getTrainingProgramName();
    Integer getYearStart();
    Integer getTotalCredits();
    String getMajorCode();
    String getMajorName();
    String getFaculty();
}
