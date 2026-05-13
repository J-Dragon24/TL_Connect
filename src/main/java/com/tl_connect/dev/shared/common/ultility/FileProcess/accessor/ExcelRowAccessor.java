package com.tl_connect.dev.shared.common.ultility.FileProcess.accessor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

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
        if (cell == null) return "";

        if (cell.getCellType() == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell)) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
        return cell.toString().trim();
    }

    @Override
    public LocalDate getDate(String columnName) {
        Cell cell = getCell(columnName);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        return parseLocalDate(cell.toString().trim());
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

    private Cell getCell(String columnName) {
        Integer colIndex = headerIdx.get(columnName.trim().toLowerCase());
        if (colIndex == null) return null;
        return row.getCell(colIndex);
    }
}

