package com.tl_connect.dev.modules.academic_result.controller;

import java.io.IOException;

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

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.ultility.FileHelper;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultAdmDTO;
import com.tl_connect.dev.modules.academic_result.dto.CreateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.UpdateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.service.AcademicResultModifyService;
import com.tl_connect.dev.modules.academic_result.service.AcademicResultService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/academic-results")
@RequiredArgsConstructor
public class AcademicResultAdminController {
    private final AcademicResultModifyService academicResultMofidyService;
    private final AcademicResultService academicResultService;
    private final FileHelper fileHelper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAcademicResult(@PageableDefault(page = 0, size = 10) Pageable pageable, @RequestParam(required = false, name = "khoa") String facultyCode) {
        PagedResponse<AcademicResultAdmDTO> result = academicResultService.getAllAcademicResult(pageable, facultyCode);
        return ResponseHelper.success("Academic result retrieved successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStudentSubjectResult(@Valid @RequestBody CreateStudentSubjectResultDTO dto) {
        Long id = academicResultMofidyService.createStudentSubjectResult(dto);
        return ResponseHelper.success("Student subject result created successfully", id);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importAcademicResult(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("File is missing");
        }
        
        if (!fileHelper.isXLSX(file) && !fileHelper.isCSV(file)) {
            throw new InvalidInputException("File must be CSV or Excel (.csv, .xlsx, .xls)");
        }
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
