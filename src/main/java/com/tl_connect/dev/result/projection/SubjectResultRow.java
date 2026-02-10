package com.tl_connect.dev.result.projection;

public interface SubjectResultRow {
    String getSemester();
    String getSubjectCode();
    String getSubjectName();
    Integer getCredits();
    Double getScore10();
    Double getScore4();
    String getLetterGrade();
    Boolean getIsPass();
}
