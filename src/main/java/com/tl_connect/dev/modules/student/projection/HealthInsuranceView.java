package com.tl_connect.dev.modules.student.projection;

import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.HealthInsuranceStatus;

public interface HealthInsuranceView {
    String getStudentCode();
    String getFullName();
    LocalDate getDateOfBirth();
    String getPhoneNumber();
    String getEmail();
    String getInsuranceNumber();
    String getProvider();
    HealthInsuranceStatus getStatus();
    LocalDate getValidFrom();
    LocalDate getValidTo();
    String getRegisteredHospital();
}
