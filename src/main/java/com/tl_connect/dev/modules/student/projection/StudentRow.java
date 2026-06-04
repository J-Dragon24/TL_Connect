package com.tl_connect.dev.modules.student.projection;

import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.Gender;
import com.tl_connect.dev.shared.common.enums.IdCardType;
import com.tl_connect.dev.shared.common.enums.StudentStatus;
import com.tl_connect.dev.shared.common.enums.TrainingType;

public interface StudentRow {
    Long getId();
    String getStudentCode();
    String getFullName();
    Gender getGender();
    LocalDate getDateOfBirth();
    String getClassCode();
    String getMajorCode();
    TrainingType getTrainingType();
    int getStartYear();
    int getEndYear();
    String getIdCardNumber();
    IdCardType getIdCardType();
    LocalDate getIssuedDate();
    String getIssuedPlace();
    String getPhoneNumber();
    String getAddress();
    String getEmail();
    String getCohort();
    String getPosition();
    String getEmergencyContactName();
    String getEmergencyContactPhoneNumber();
    String getEmergencyContactAddress();
    String getRelationship();
    StudentStatus getStatus();
}
