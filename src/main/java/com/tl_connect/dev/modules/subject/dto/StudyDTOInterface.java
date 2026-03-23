package com.tl_connect.dev.modules.subject.dto;

import java.math.BigDecimal;

public interface StudyDTOInterface {
    String getSubjectCode();
    String getSubjectName();
    Integer getCredits();
    BigDecimal getCoefficient();
    Integer getLectureHours();
    Integer getPracticeHours();
    Long getFacultyId();
    Long getDepartmentId();
}
