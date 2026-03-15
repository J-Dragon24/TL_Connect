package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.IdCardType;

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
public class IdentityCardDTO {
    @ImportColumn("Số CCCD/CMND")
    private String cardNumber;
    @ImportColumn("Loại thẻ")
    private IdCardType cardType;
    @ImportColumn("Ngày đăng ký")
    private LocalDate issuedDate;
    @ImportColumn("Nơi đăng ký")
    private String issuedPlace;
}
