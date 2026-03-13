package com.tl_connect.dev.core.common.ultility.importer;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RowAccesor {
    String getString(String columnName);
    Integer getInteger(String columnName);
    BigDecimal getBigDecimal(String columnName);
    LocalDate getDate(String columnName);
    Boolean getBoolean(String columnName);
}
