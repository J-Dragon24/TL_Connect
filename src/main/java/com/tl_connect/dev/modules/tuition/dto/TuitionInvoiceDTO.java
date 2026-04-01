package com.tl_connect.dev.modules.tuition.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.TuitionStatus;

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
public class TuitionInvoiceDTO {
    private Long invoiceId;
    private String semesterName;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    private TuitionStatus status;
    private LocalDate dueDate;
}
