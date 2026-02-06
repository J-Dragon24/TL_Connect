package com.tl_connect.dev.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.common.enums.IdCardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityCardDTO {
    private String cardNumber;
    private IdCardType cardType;
    private LocalDate issuedDate;
    private String issuedPlace;
}
