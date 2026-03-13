package com.tl_connect.dev.core.common.ultility.importer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

public class ExcelRowAccessor implements RowAccesor {
    private final Row row;
    private final Map<String, Integer> headerIdx;

    public ExcelRowAccessor(Row row, Map<String, Integer> headerIdx) {
        this.row = row;
        this.headerIdx = headerIdx;
    }

    @Override
    public String getString(String col) {
        Integer idx = headerIdx.get(col.toLowerCase());
        if(idx == null) return "";
        Cell cell = row.getCell(idx);
        return cell == null ? "" : cell.toString().trim();
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

