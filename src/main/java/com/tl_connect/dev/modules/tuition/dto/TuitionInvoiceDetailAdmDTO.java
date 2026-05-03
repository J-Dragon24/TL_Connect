package com.tl_connect.dev.modules.tuition.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.TuitionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TuitionInvoiceDetailAdmDTO {
    private Long invoiceId;
    private String studentName;
    private String studentCode;
    private String semesterCode;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    private TuitionStatus status;
    private LocalDate dueDate;
    private List<TuitionItemDTO> items;
}
