package com.tl_connect.dev.modules.student.projection;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.EducationMode;
import com.tl_connect.dev.core.common.enums.Gender;
import com.tl_connect.dev.core.common.enums.IdCardType;

public interface StudentInfoView {
    String getStudentCode();
    String getFullName();
    Gender getGender();
    LocalDate getDateOfBirth();
    String getClassCode();
    String getAcademicAdvisor();

    //major
    String getMajorCode();
    String getMajorName();
    String getFaculty();
    
    //identity card
    String getIdCardNumber();
    IdCardType getIdCardType();
    LocalDate getIssuedDate();
    String getIssuedPlace();

    //contact
    String getPhoneNumber();
    String getAdress();
    String getEmail();

    //academic info
    String getCohort();
    String getPosition();
    EducationMode getEducationMode();

    //emergency contact
    String getEmergencyContactName();
    String getEmergencyContactPhoneNumber();
    String getEmergencyContactAdress();
}
