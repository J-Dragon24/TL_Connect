package com.tl_connect.dev.modules.student.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.modules.student.service.StudentWriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/student")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentWriteService studentWriteService;
    
    @PostMapping("/import")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file) throws IOException {
        ImportResultDTO result = studentWriteService.importFile(file);
        return ResponseHelper.success("File imported successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStudent(@RequestBody StudentImportDTO dto) {
        Long studentId = studentWriteService.createStudent(dto);
        return ResponseHelper.success("Student created successfully", studentId);
    }
}
