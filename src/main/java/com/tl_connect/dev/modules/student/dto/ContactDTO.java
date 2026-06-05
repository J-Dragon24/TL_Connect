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
public class ContactDTO {
    @ExcelColumn(header = "Số điện thoại")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;
    @ExcelColumn(header = "Địa chỉ")
    private String address;
    @ExcelColumn(header = "Email")
    private String email;
}
