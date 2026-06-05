package com.tl_connect.dev.modules.student.controller;

import java.io.IOException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.student.dto.StudentFullInfo;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.modules.student.dto.UpdateBasicInfoDTO;
import com.tl_connect.dev.modules.student.dto.UpdateStudentAcademicDTO;
import com.tl_connect.dev.modules.student.service.interfaces.StudentDeleteService;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;
import com.tl_connect.dev.modules.student.service.interfaces.StudentUpdateService;
import com.tl_connect.dev.modules.student.service.interfaces.StudentWriteService;
import com.tl_connect.dev.shared.common.dto.ImportResultDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.ultility.FileHelper;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/students")
@RequiredArgsConstructor
public class StudentAdminController {

    private final StudentWriteService studentWriteService;
    private final StudentUpdateService studentUpdateService;
    private final StudentDeleteService studentDeleteService;
    private final StudentService studentService;
    private final FileHelper fileHelper;

    @PostMapping("/import")
    public ResponseEntity<?> importFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("File is missing");
        }
        
        if (!fileHelper.isXLSX(file) && !fileHelper.isCSV(file)) {
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
    public ResponseEntity<?> getAllStudents(@PageableDefault(page = 0, size = 50) Pageable pageable, @RequestParam(required = false, name = "khoa") String facultyCode) {
        PagedResponse<StudentFullInfo> result = studentService.getAllStudents(pageable, facultyCode);
        return ResponseHelper.success("Get all students successfully", result);
    }

    @PostMapping("/update/{studentId}/basic")
    public ResponseEntity<?> updateStudentBasicInfo(@PathVariable Long studentId, @RequestBody @Valid UpdateBasicInfoDTO dto) {
        studentUpdateService.updateBasicInfo(studentId, dto);
        return ResponseHelper.success("Student updated successfully", studentId);
    }

    @PostMapping("/update/{studentId}/academic")
    public ResponseEntity<?> updateStudentAcademicInfo(@PathVariable Long studentId, @RequestBody @Valid UpdateStudentAcademicDTO dto) {
        studentUpdateService.updateAcademicInfo(studentId, dto);
        return ResponseHelper.success("Student academic info updated successfully", studentId);
    }

    @PostMapping("/delete/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long studentId) {
        studentDeleteService.deleteStudent(studentId);
        return ResponseHelper.success("Student deleted successfully", studentId);
    }
}
