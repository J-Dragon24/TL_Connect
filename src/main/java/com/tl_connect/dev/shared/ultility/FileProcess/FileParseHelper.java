package com.tl_connect.dev.shared.ultility.FileProcess;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.ultility.FileProcess.accessor.CsvRowAccessor;
import com.tl_connect.dev.shared.ultility.FileProcess.accessor.ExcelRowAccessor;
import com.tl_connect.dev.shared.ultility.FileProcess.accessor.RowAccessor;
import com.tl_connect.dev.shared.ultility.FileProcess.annotation.ExcelColumn;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public abstract class FileParseHelper {
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
            if (isRowEmpty(row))
                continue;
            result.add(mapToObject(new ExcelRowAccessor(row, headerIdx), clazz));
        }
        workbook.close();
        return result;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int cellNum = row.getFirstCellNum();
            cellNum < row.getLastCellNum();
            cellNum++) {

            Cell cell = row.getCell(cellNum);

            if (cell != null &&
                cell.getCellType() != CellType.BLANK &&
                !cell.toString().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private <T> T mapToObject(RowAccessor row, Class<T> clazz) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                ExcelColumn col = field.getAnnotation(ExcelColumn.class);

                if (col != null) {
                    if (field.getType() == LocalDate.class) {
                        field.set(obj, row.getDate(col.header()));
                    } else {
                        field.set(obj, convertValue(row.getString(col.header()), field.getType()));
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

    //EXPORT EXCEL

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;

    public void newExcel() {
        workbook = new XSSFWorkbook();
    }

    
    public HttpServletResponse initResponseForExportExcel(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName + "_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        return response;
    }


    public <T> void exportToSheet(String sheetName, String titleName, List<T> data, Class<T> clazz) {
        sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        // Lấy các field có @ExcelColumn, sort theo order
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ExcelColumn.class)
                        && f.getAnnotation(ExcelColumn.class).exportable())
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(ExcelColumn.class).order()))
                .toList();

        Row titleRow = sheet.createRow(0);

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        createCell(titleRow, 0, titleName, headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, fields.size() - 1));
        font.setFontHeightInPoints((short) 10);

        Row headerRow = sheet.createRow(1);

        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeight(16);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < fields.size(); i++) {
            ExcelColumn col = fields.get(i).getAnnotation(ExcelColumn.class);
            createCell(headerRow, i, col.header(), headerStyle);
        }

        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontHeight(14);
        dataStyle.setFont(dataFont);

        // Data rows
        for (T item : data) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int i = 0; i < fields.size(); i++) {
                fields.get(i).setAccessible(true);
                try {
                    Object value = fields.get(i).get(item);
                    createCell(row, i, value, dataStyle);
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDate d) {
            cell.setCellValue(d.toString());
        } else {
            cell.setCellValue(value.toString());
        }
        if (style != null) cell.setCellStyle(style);
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

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        s.setFont(font);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Arial");
        s.setFont(font);
        return s;
    }
}
