package com.tl_connect.dev.modules.tuition.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.TuitionStatus;

public interface TuitionInvoiceRow {
    Long getId();
    Long getStudentId();
    String getSemesterCode();
    String getStudentName();
    String getStudentCode();
    BigDecimal getTotalAmount();
    BigDecimal getFinalAmount();
    TuitionStatus getStatus();
    LocalDate getDueDate();
}
