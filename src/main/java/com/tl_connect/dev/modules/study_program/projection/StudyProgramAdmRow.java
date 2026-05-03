package com.tl_connect.dev.modules.study_program.projection;

import com.tl_connect.dev.shared.common.enums.TrainingType;

public interface StudyProgramAdmRow {
    Long getId();
    String getStudyProgramCode();
    String getStudyProgramName();
    String getMajorCode();
    Integer getStartYear();
    Integer getTotalCredits();
    TrainingType getTrainingType();
}
