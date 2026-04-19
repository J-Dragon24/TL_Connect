package com.tl_connect.dev.modules.academic_result.projection;

import java.math.BigDecimal;

public interface SubjectResultAdmRow {
    Long getStudentId();
    String getStudentCode();
    String getStudentName();
    String getStartYear();
    String getMajorName();
    String getStudyProgramCode();
    String getStudyProgramName();
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
