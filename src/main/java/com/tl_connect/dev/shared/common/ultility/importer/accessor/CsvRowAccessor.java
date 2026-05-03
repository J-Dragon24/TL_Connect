package com.tl_connect.dev.shared.common.ultility.importer.accessor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class CsvRowAccessor implements RowAccessor {
    private final String[] values;
    private final Map<String, Integer> headerIdx;

    public CsvRowAccessor(String[] values, Map<String, Integer> headerIdx) {
        this.values = values;
        this.headerIdx = headerIdx;
    }

    @Override
    public String getString(String col) {
        Integer idx = headerIdx.get(col.toLowerCase());
        if(idx == null || idx >= values.length) return "";
        String val = values[idx];
        return val == null ? "" : val.trim();
    }

    @Override
    public LocalDate getDate(String columnName) {
        return parseLocalDate(getString(columnName));
    }

    private LocalDate parseLocalDate(String val) {
        if (val == null || val.isBlank()) return null;

        List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(val.trim(), formatter);
            } catch (Exception ignored) {}
        }

        throw new IllegalArgumentException("Ngày không hợp lệ: '" + val + "' — định dạng hợp lệ: dd/MM/yyyy");
    }
}
