package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.Gender;

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
public class StudentImportDTO {
    private String studentCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String studentClassCode;
    private String majorCode;
    private IdentityCardDTO identityCard;
    private ContactDTO contact;
    private AcademicInfoDTO academicInfo;
    private EmergencyContactDTO emergencyContact;
}
