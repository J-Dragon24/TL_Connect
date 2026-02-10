package com.tl_connect.dev.result.projection;

public interface SemesterSummaryView {
    String getSemester();
    Integer getCreditsRegistered();
    Integer getCreditsPassed();
    Double getSemesterGpa();
    Integer getConductScore();
}
