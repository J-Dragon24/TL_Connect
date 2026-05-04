package com.tl_connect.dev.modules.enroll.projection;

import java.math.BigDecimal;

public interface SubjectForEnrollRow {
    Long getStudyProgramId();
    String getStudyProgramCode();
    String getStudyProgramName();
    Long getSubjectId();
    String getFacultyName();
    String getFacultyCode();
    String getDepartmentName();
    String getDepartmentCode();
    String getSubjectCode();
    String getSubjectName();
    Integer getCredits();
    Boolean getIsRequired();
    String getElectiveGroup();
    BigDecimal getCoefficient();
    Integer getLectureHours();
    Integer getPracticeHours();
}