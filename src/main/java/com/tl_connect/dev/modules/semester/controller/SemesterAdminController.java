package com.tl_connect.dev.modules.semester.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.semester.SemesterService;
import com.tl_connect.dev.modules.semester.dto.CreateSemesterDTO;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.semester.dto.UpdateSemesterDTO;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/semesters")
@RequiredArgsConstructor
public class SemesterAdminController {
    
    private final SemesterService semesterService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSemesters(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SemesterDTO> result = semesterService.getAll(pageable);
        return ResponseHelper.success("Semesters retrieved successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSemester(@RequestBody CreateSemesterDTO createSemesterDTO){
        Long id = semesterService.createSemester(createSemesterDTO);
        return ResponseHelper.success("Semester created successfully", id);
    }

    @PostMapping("update/{id}")
    public ResponseEntity<?> updateSemester(@PathVariable Long id, @RequestBody UpdateSemesterDTO updateSemesterDTO){
        semesterService.updateSemester(id, updateSemesterDTO);
        return ResponseHelper.success("Semester updated successfully", null);
    }

    @PostMapping("delete/{id}")
    public ResponseEntity<?> deleteSemester(@PathVariable Long id){
        semesterService.deleteSemester(id);
        return ResponseHelper.success("Semester deleted successfully", null);
    }
}
