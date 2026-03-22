package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDTO {
    @ImportColumn("Tên người liên hệ")
    private String name;
    @ImportColumn("Số điện thoại 2")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid emergency phone")
    private String phoneNumber;
    @ImportColumn("Địa chỉ 2")
    private String address;
    @ImportColumn("Mối quan hệ")
    private String relationship;
}
