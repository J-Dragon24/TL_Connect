package com.tl_connect.dev.modules.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tl_connect.dev.shared.common.ultility.importer.annotation.ImportColumn;

import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    @ImportColumn("Số điện thoại")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;
    @ImportColumn("Địa chỉ")
    private String address;
    @ImportColumn("Email")
    private String email;
}
