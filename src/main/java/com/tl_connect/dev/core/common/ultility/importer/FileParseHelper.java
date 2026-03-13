package com.tl_connect.dev.core.common.ultility.importer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileParseHelper {
    public <T> List<T> parse(MultipartFile file, ColumnMapper<T> mapper) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }
        if (fileName.endsWith(".csv")) {
            return parseCSV(file, mapper);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return parseExcel(file, mapper);
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private <T> List<T> parseCSV(MultipartFile file, ColumnMapper<T> mapper) throws IOException {
        List<T> result = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] headers = reader.readNext();
            Map<String, Integer> headerIdx = new HashMap<>();
            for( int i=0; i< headers.length; i++){
                headerIdx.put(headers[i].trim().toLowerCase(), i);
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                result.add(mapper.map(new CsvRowAccessor(line, headerIdx)));
            }
        } catch (CsvValidationException e) {
            throw new IOException("Failed to parse CSV file: " + e.getMessage(), e);
        }
        return result;
    }

    private <T> List<T> parseExcel(MultipartFile file, ColumnMapper<T> mapper) throws IOException {
        List<T> result = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        Map<String, Integer> headerIdx = new HashMap<>();

        for(Cell cell : headerRow){
            headerIdx.put(cell.toString().trim().toLowerCase(), cell.getColumnIndex());
        }

        for( int i = 1; i< sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if(row == null) continue;
            result.add(mapper.map(new ExcelRowAccessor(row, headerIdx)));
        }
        workbook.close();
        return result;
    }
}
