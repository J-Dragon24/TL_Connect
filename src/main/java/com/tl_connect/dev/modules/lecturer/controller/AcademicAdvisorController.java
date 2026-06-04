package com.tl_connect.dev.modules.lecturer.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDTO;
import com.tl_connect.dev.modules.lecturer.dto.CreateAcademicAdvisorDTO;
import com.tl_connect.dev.modules.lecturer.service.interfaces.AcademicAdvisorService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/academic-advisors")
@RequiredArgsConstructor
public class AcademicAdvisorController {

    private final AcademicAdvisorService academicAdvisorService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<AcademicAdvisorDTO> response = academicAdvisorService.getAll(pageable);
        return ResponseHelper.success("Get all academic advisors successfully", response);
    }

    @GetMapping("/{lecturerId}")
    public ResponseEntity<?> getDetail(@PathVariable(name = "lecturerId") Long lecturerId) {
        return ResponseHelper.success("Get detail academic advisor successfully",
                academicAdvisorService.getById(lecturerId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CreateAcademicAdvisorDTO dto) {
        Long id = academicAdvisorService.create(dto);
        return ResponseHelper.success("Create academic advisor successfully", id);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        academicAdvisorService.delete(id);
        return ResponseHelper.success("Delete academic advisor successfully", null);
    }
}
