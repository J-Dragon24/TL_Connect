package com.tl_connect.dev.shared.ultility.FileProcess.accessor;

import java.time.LocalDate;

public interface RowAccessor {
    String getString(String columnName);
    LocalDate getDate(String columnName);
}
