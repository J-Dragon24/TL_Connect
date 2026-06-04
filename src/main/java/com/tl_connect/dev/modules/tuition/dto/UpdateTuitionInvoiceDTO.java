package com.tl_connect.dev.modules.tuition.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.TuitionStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTuitionInvoiceDTO {
    private List<Long> subjectIds;
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;
    @NotNull(message = "Final amount is required")
    private BigDecimal finalAmount;
    @NotNull(message = "Status is required")
    private TuitionStatus status;
}
