package com.tl_connect.dev.modules.student.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.Gender;
import com.tl_connect.dev.shared.common.enums.TrainingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoDTO implements Serializable {
    private String studentCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String classCode;
    private String academicAdvisor;
    private int startYear;
    private int endYear;
    private TrainingType trainingType;
    private MajorDTO major;
    private IdentityCardDTO identityCard;
    private ContactDTO contact;
    private AcademicInfoDTO academicInfo;
    private EmergencyContactDTO emergencyContact;
}