package com.tl_connect.dev.shared.common.ultility.importer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.ultility.importer.accessor.CsvRowAccessor;
import com.tl_connect.dev.shared.common.ultility.importer.accessor.ExcelRowAccessor;
import com.tl_connect.dev.shared.common.ultility.importer.accessor.RowAccessor;
import com.tl_connect.dev.shared.common.ultility.importer.annotation.ImportColumn;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileParseHelper {
    public <T> List<T> parse(MultipartFile file, Class<T> clazz) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new InvalidInputException("File name is null");
        }
        if (fileName.endsWith(".csv")) {
            return parseCSV(file, clazz);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return parseExcel(file, clazz);
        } else {
            throw new InvalidInputException("File must be CSV or Excel (.csv, .xlsx, .xls)");
        }
    }

    private <T> List<T> parseCSV(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> result = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            Map<String, Integer> headerIdx = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIdx.put(headers[i].trim().toLowerCase(), i);
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                result.add(mapToObject(new CsvRowAccessor(line, headerIdx), clazz));
            }
        } catch (CsvValidationException e) {
            throw new IOException("Failed to parse CSV file: " + e.getMessage(), e);
        }
        return result;
    }

    private <T> List<T> parseExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> result = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerIdx = new HashMap<>();

        for (Cell cell : headerRow) {
            headerIdx.put(cell.toString().trim().toLowerCase(), cell.getColumnIndex());
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;
            result.add(mapToObject(new ExcelRowAccessor(row, headerIdx), clazz));
        }
        workbook.close();
        return result;
    }

    private <T> T mapToObject(RowAccessor row, Class<T> clazz) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                ImportColumn col = field.getAnnotation(ImportColumn.class);

                if (col != null) {
                    if (field.getType() == LocalDate.class) {
                        field.set(obj, row.getDate(col.value()));
                    } else {
                        field.set(obj, convertValue(row.getString(col.value()), field.getType()));
                    }
                } else if (!isPrimitive(field.getType())) {
                    field.set(obj, mapToObject(row, field.getType()));
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mapping row: " + e.getMessage());
        }
    }

    private Object convertValue(String val, Class<?> type) {
        if (val == null || val.isBlank())
            return null;
        if (type == String.class)
            return val;
        if (type == Integer.class)
            return (int) Double.parseDouble(val);
        if (type == Long.class)
            return (long) Double.parseDouble(val);
        if (type == Double.class)
            return Double.parseDouble(val);
        if (type == BigDecimal.class)
            return new BigDecimal(val);
        if (type == Boolean.class)
            return Boolean.valueOf(val);
        if (type == LocalDate.class)
            return LocalDate.parse(val);
        if (type == LocalDateTime.class)
            return LocalDateTime.parse(val);
        if (type.isEnum())
            return parseEnum(type, val);
        return val;
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T parseEnum(Class<?> type, String val) {
        Class<T> enumType = (Class<T>) type;
        return Enum.valueOf(enumType, val);
    }

    private boolean isPrimitive(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == BigDecimal.class
                || type == Boolean.class
                || type == LocalDate.class
                || type == LocalDateTime.class
                || type.isEnum();
    }
}
