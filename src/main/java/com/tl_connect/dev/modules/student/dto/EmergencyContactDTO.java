package com.tl_connect.dev.modules.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tl_connect.dev.shared.ultility.FileProcess.annotation.ExcelColumn;

import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDTO {
    @ExcelColumn(header = "Tên người liên hệ")
    private String name;
    @ExcelColumn(header = "Số điện thoại 2")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid emergency phone")
    private String phoneNumber;
    @ExcelColumn(header = "Địa chỉ 2")
    private String address;
    @ExcelColumn(header = "Mối quan hệ")
    private String relationship;
}
