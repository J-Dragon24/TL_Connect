package com.tl_connect.dev.modules.academic_result.projection;

import java.math.BigDecimal;

public interface SubjectResultRow {
    String getSemester();
    String getSubjectCode();
    String getSubjectName();
    Integer getCredits();
    BigDecimal getAttendanceScore();
    BigDecimal getMidtermScore();
    BigDecimal getFinalScore();
    BigDecimal getScore10();
    BigDecimal getScore4();
    String getLetterGrade();
    Boolean getIsPass();
}
