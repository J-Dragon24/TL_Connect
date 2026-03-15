package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.Gender;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;
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

    @ImportColumn("Mã sinh viên")
    private String studentCode;
    @ImportColumn("Họ tên")
    private String fullName;
    @ImportColumn("Ngày sinh")
    private LocalDate dateOfBirth;
    @ImportColumn("Giới tính")
    private Gender gender;
    @ImportColumn("Mã lớp")
    private String studentClassCode;
    @ImportColumn("Mã ngành")
    private String majorCode;
    private IdentityCardDTO identityCard;
    private ContactDTO contact;
    private AcademicInfoDTO academicInfo;
    private EmergencyContactDTO emergencyContact;
}
