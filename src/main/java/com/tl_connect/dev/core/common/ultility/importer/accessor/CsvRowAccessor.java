package com.tl_connect.dev.core.common.ultility.importer.accessor;

import com.tl_connect.dev.core.common.ultility.importer.RowAccessor;

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
}
