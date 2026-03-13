package com.tl_connect.dev.core.common.ultility.importer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


public class CsvRowAccessor implements RowAccesor{
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
    public BigDecimal getBigDecimal(String col) {
        String val = getString(col);
        return val.isBlank() ? BigDecimal.ZERO : new BigDecimal(val);
    }

    @Override
    public Boolean getBoolean(String col) {
        String val = getString(col);
        return val.isBlank() ? false : Boolean.valueOf(val);
    }

    @Override
    public LocalDate getDate(String col) {
        String val = getString(col);
        return val.isBlank() ? null : LocalDate.parse(val);
    }

    @Override
    public Integer getInteger(String col) {
        String val = getString(col);
        return val.isBlank() ? 0 : (int) Double.parseDouble(val);
    }
}
