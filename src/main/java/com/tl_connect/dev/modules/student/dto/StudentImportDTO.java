package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.Gender;
import com.tl_connect.dev.core.common.enums.TrainingType;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @ImportColumn("Họ tên")
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh không hợp lệ")
    @ImportColumn("Ngày sinh")
    private LocalDate dateOfBirth;

    @NotNull(message = "Giới tính không được để trống")
    @ImportColumn("Giới tính")
    private Gender gender;

    @NotBlank(message = "Mã lớp không được để trống")
    @ImportColumn("Mã lớp")
    private String studentClassCode;

    @NotBlank(message = "Mã ngành không được để trống")
    @ImportColumn("Mã ngành")
    private String majorCode;

    @NotNull(message = "Năm nhập học không được để trống")
    @ImportColumn("Năm nhập học")
    private Integer startYear;

    @NotNull(message = "Năm tốt nghiệp dự kiến không được để trống")
    @ImportColumn("Năm tốt nghiệp dự kiến")
    private Integer endYear;

    @NotNull(message = "Hình thức đào tạo không được để trống")
    @ImportColumn("Hình thức đào tạo")
    private TrainingType trainingType;

    @Valid @NotNull
    private IdentityCardDTO identityCard;

    @Valid @NotNull
    private ContactDTO contact;

    @Valid @NotNull
    private AcademicInfoDTO academicInfo;

    @Valid @NotNull
    private EmergencyContactDTO emergencyContact;
}
