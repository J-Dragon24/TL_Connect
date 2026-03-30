package com.tl_connect.dev.modules.faculty;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.faculty.dto.FacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.UpdateFacultyDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllFaculties(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<FacultyDTO> response = facultyService.getAllFaculties(pageable);
        return ResponseHelper.success("Get all faculties successfully", response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFaculty(@Valid @RequestBody FacultyDTO facultyDTO) {
        Long id = facultyService.createFaculty(facultyDTO);
        return ResponseHelper.success("Create faculty successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateFaculty(@PathVariable Long id, @Valid @RequestBody UpdateFacultyDTO facultyDTO) {
        facultyService.updateFaculty(id, facultyDTO);
        return ResponseHelper.success("Update faculty successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseHelper.success("Delete faculty successfully", null);
    }
}
