package com.tl_connect.dev.modules.major;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.major.dto.CreateMajorDTO;
import com.tl_connect.dev.modules.major.dto.MajorAdmDTO;
import com.tl_connect.dev.modules.major.dto.UpdateMajorDTO;
import com.tl_connect.dev.modules.major.entity.Major;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/majors")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMajors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<MajorAdmDTO> result = majorService.getAllMajors(pageable);
        return ResponseHelper.success("Get all majors successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMajor(@Valid @RequestBody CreateMajorDTO dto) {
        Major major = majorService.createMajor(dto);
        return ResponseHelper.success("Create major successfully", major);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateMajor(@PathVariable Long id, @Valid @RequestBody UpdateMajorDTO dto) {
        majorService.updateMajor(id, dto);
        return ResponseHelper.success("Update major successfully", dto);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ResponseHelper.success("Delete major successfully", null);
    }
}
