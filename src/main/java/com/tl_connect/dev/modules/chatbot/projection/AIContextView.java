package com.tl_connect.dev.modules.chatbot.projection;

import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.Gender;

public interface AIContextView {
    String getStudentName();
    String getStudentCode();
    LocalDate getDateOfBirth();
    Gender getGender();
    int getStartYear();
    int getEndYear();
    String getMajorCode();
    String getMajorName();
    String getFacultyCode();
    String getStudyProgramCode();
}
