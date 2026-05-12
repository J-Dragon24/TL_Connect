package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.Gender;
import com.tl_connect.dev.shared.common.enums.TrainingType;
import com.tl_connect.dev.shared.common.ultility.importer.annotation.ExcelColumn;

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

    @ExcelColumn(header = "Mã sinh viên")
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @ExcelColumn(header = "Họ tên")
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh không hợp lệ")
    @ExcelColumn(header = "Ngày sinh")
    private LocalDate dateOfBirth;

    @NotNull(message = "Giới tính không được để trống")
    @ExcelColumn(header = "Giới tính")
    private Gender gender;

    @NotBlank(message = "Mã lớp không được để trống")
    @ExcelColumn(header = "Mã lớp")
    private String studentClassCode;

    @NotBlank(message = "Mã ngành không được để trống")
    @ExcelColumn(header = "Mã ngành")
    private String majorCode;

    @NotNull(message = "Năm nhập học không được để trống")
    @ExcelColumn(header = "Năm nhập học")
    private Integer startYear;

    @NotNull(message = "Năm tốt nghiệp dự kiến không được để trống")
    @ExcelColumn(header = "Năm tốt nghiệp dự kiến")
    private Integer endYear;

    @NotNull(message = "Hình thức đào tạo không được để trống")
    @ExcelColumn(header = "Hình thức đào tạo")
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
