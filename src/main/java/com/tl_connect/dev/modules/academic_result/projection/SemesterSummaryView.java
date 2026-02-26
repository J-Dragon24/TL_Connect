package com.tl_connect.dev.modules.academic_result.projection;

import java.math.BigDecimal;

public interface SemesterSummaryView {
    String getSemester();
    Integer getCreditsRegistered();
    Integer getCreditsPassed();
    BigDecimal getSemesterGpa();
    Integer getConductScore();
    BigDecimal getCumulativeGpa();
}
