package com.tl_connect.dev.modules.academic_result.projection;

import java.math.BigDecimal;

public interface SemesterSummaryRow {
    Long getStudentId();
    String getSemester();
    String getStudyProgramCode();
    Integer getCreditsRegistered();
    Integer getCreditsPassed();
    BigDecimal getSemesterGpa();
    Integer getConductScore();
}