package com.tl_connect.dev.modules.tuition.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionItemDTO {
    private Long id;
    private String subjectName;
    private String subjectCode;
    private int credits;
    private BigDecimal pricePerCredit;
    private BigDecimal coefficient;
    private BigDecimal amount;
    private boolean isRetake;
}
