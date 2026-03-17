package com.tl_connect.dev.modules.student.controller;

import java.io.IOException;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.modules.student.dto.StudentInfoDTO;
import com.tl_connect.dev.modules.student.service.StudentService;
import com.tl_connect.dev.modules.student.service.StudentWriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/student")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentWriteService studentWriteService;
    private final StudentService studentService;

    private static final int PAGE_SIZE = 50;

    @PostMapping("/import")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("File is missing");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new InvalidInputException("File name is null");
        }

        String lower = fileName.toLowerCase();
        if (!lower.endsWith(".csv") && !lower.endsWith(".xlsx") && !lower.endsWith(".xls")) {
            throw new InvalidInputException("File must be CSV or Excel (.csv, .xlsx, .xls)");
        }
 
        ImportResultDTO result = studentWriteService.importFile(file);
        return ResponseHelper.success("File imported successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStudent(@RequestBody @Valid StudentImportDTO dto) {
        Long studentId = studentWriteService.createStudent(dto);
        return ResponseHelper.success("Student created successfully", studentId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        PagedResponse<StudentInfoDTO> result = studentService.getAllStudents(pageable);
        return ResponseHelper.success("Get all students successfully", result);
    }
}
