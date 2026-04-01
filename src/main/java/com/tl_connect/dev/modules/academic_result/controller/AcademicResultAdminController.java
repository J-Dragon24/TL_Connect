package com.tl_connect.dev.modules.academic_result.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.academic_result.dto.CreateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.UpdateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.service.AcademicResultModifyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/academic-results")
@RequiredArgsConstructor
public class AcademicResultAdminController {
    private final AcademicResultModifyService academicResultMofidyService;

    @PostMapping("/create")
    public ResponseEntity<?> createStudentSubjectResult(@Valid @RequestBody CreateStudentSubjectResultDTO dto) {
        Long id = academicResultMofidyService.createStudentSubjectResult(dto);
        return ResponseHelper.success("Student subject result created successfully", id);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importAcademicResult(@RequestParam("file") MultipartFile file) throws IOException {
        ImportResultDTO result = academicResultMofidyService.importFile(file);
        return ResponseHelper.success("Academic result imported successfully", result);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateStudentSubjectResult(@PathVariable Long id, @Valid @RequestBody UpdateStudentSubjectResultDTO dto) {
        academicResultMofidyService.updateStudentSubjectResult(id, dto);
        return ResponseHelper.success("Student subject result updated successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteStudentSubjectResult(@PathVariable Long id) {
        academicResultMofidyService.deleteStudentSubjectResult(id);
        return ResponseHelper.success("Student subject result deleted successfully", null);
    }
}
