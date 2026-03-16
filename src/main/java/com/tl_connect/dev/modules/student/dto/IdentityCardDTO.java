package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.IdCardType;

import com.tl_connect.dev.core.common.ultility.importer.annotation.ImportColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Số CCCD/CMND không được để trống")
    private String cardNumber;

    @NotNull(message = "Loại thẻ không được để trống")
    @ImportColumn("Loại thẻ")
    private IdCardType cardType;
    @ImportColumn("Ngày cấp")
    private LocalDate issuedDate;
    @ImportColumn("Nơi cấp")
    private String issuedPlace;
}
