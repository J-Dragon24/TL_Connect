package com.tl_connect.dev.modules.tuition.projection;

import java.math.BigDecimal;

public interface TuitionItemProjection {
    Long getId();
    String getSubjectName();
    String getSubjectCode();
    Integer getCredits();
    BigDecimal getPricePerCredit();
    BigDecimal getCoefficient();
    BigDecimal getAmount();
    Boolean getIsRetake();
}
