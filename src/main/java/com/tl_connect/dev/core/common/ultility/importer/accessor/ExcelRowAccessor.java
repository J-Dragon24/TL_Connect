package com.tl_connect.dev.core.common.ultility.importer.accessor;

import java.util.Map;

import com.tl_connect.dev.core.common.ultility.importer.RowAccessor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

public class ExcelRowAccessor implements RowAccessor {
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
}

