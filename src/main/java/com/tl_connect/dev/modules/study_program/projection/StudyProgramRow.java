package com.tl_connect.dev.modules.study_program.projection;

public interface StudyProgramRow {
    Long getId();
    
    String getStudentCode();

    String getStudyProgramCode();

    String getStudyProgramName();

    Boolean getIsPrimary();
}
