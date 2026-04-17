package com.tl_connect.dev.modules.lecturer.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.lecturer.dto.CreateLecturerDTO;
import com.tl_connect.dev.modules.lecturer.dto.LecturerAdmInfoDTO;
import com.tl_connect.dev.modules.lecturer.dto.UpdateLecturerDTO;
import com.tl_connect.dev.modules.lecturer.service.LecturerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@PageableDefault(page = 0, size = 10) Pageable pageable, @RequestParam(required = false, name= "khoa") String facultyCode) {
        PagedResponse<LecturerAdmInfoDTO> response = lecturerService.getAllLecturers(pageable, facultyCode);
        return ResponseHelper.success("Get all lecturers successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseHelper.success("Get detail lecturer successfully", lecturerService.getLecturerInfo(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CreateLecturerDTO dto) {
        return ResponseHelper.success("Create lecturer successfully", lecturerService.createLecturer(dto));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateLecturerDTO dto) {
        lecturerService.updateLecturer(id, dto);
        return ResponseHelper.success("Update lecturer successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseHelper.success("Delete lecturer successfully", null);
    }
}
