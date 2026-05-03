package com.tl_connect.dev.modules.major;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.major.dto.CreateMajorDTO;
import com.tl_connect.dev.modules.major.dto.MajorAdmDTO;
import com.tl_connect.dev.modules.major.dto.UpdateMajorDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/majors")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMajors(@PageableDefault(page = 0, size = 10) Pageable pageable, @RequestParam(required = false, name = "khoa") String facultyCode) {
        PagedResponse<MajorAdmDTO> result = majorService.getAllMajors(pageable, facultyCode);
        return ResponseHelper.success("Get all majors successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMajor(@Valid @RequestBody CreateMajorDTO dto) {
        Long id = majorService.createMajor(dto);
        return ResponseHelper.success("Create major successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateMajor(@PathVariable Long id, @Valid @RequestBody UpdateMajorDTO dto) {
        majorService.updateMajor(id, dto);
        return ResponseHelper.success("Update major successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ResponseHelper.success("Delete major successfully", null);
    }
}
