package com.tl_connect.dev.modules.academic_result.projection;

public interface SemesterSummaryView {
    String getSemester();
    Integer getCreditsRegistered();
    Integer getCreditsPassed();
    Double getSemesterGpa();
    Integer getConductScore();
    Double getCumulativeGpa();
}
