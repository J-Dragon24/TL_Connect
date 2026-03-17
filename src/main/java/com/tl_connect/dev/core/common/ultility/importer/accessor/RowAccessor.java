package com.tl_connect.dev.core.common.ultility.importer.accessor;

import java.time.LocalDate;

public interface RowAccessor {
    String getString(String columnName);
    LocalDate getDate(String columnName);
}
