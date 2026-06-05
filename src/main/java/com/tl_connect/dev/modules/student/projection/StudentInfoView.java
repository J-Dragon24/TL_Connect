package com.tl_connect.dev.modules.student.projection;

import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.Gender;
import com.tl_connect.dev.shared.common.enums.IdCardType;
import com.tl_connect.dev.shared.common.enums.TrainingType;

public interface StudentInfoView {
    String getStudentCode();
    String getFullName();
    String getAvatarUrl();
    Gender getGender();
    LocalDate getDateOfBirth();
    String getClassCode();
    String getAcademicAdvisor();
    int getStartYear();
    int getEndYear();

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
    String getAddress();
    String getEmail();

    //academic info
    String getCohort();
    String getPosition();
    TrainingType getTrainingType();

    //emergency contact
    String getEmergencyContactName();
    String getEmergencyContactPhoneNumber();
    String getEmergencyContactAddress();
    String getRelationship();
}
