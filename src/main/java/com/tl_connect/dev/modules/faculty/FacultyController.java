package com.tl_connect.dev.modules.faculty;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.faculty.dto.CreateFacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.FacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.UpdateFacultyDTO;
import com.tl_connect.dev.modules.faculty.service.interfaces.FacultyService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllFaculties(Authentication authentication, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<FacultyDTO> response = facultyService.getAllFaculties(pageable);
        return ResponseHelper.success("Get all faculties successfully", response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFaculty(Authentication authentication, @Valid @RequestBody CreateFacultyDTO facultyDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long id = facultyService.createFaculty(facultyDTO);
        return ResponseHelper.success("Create faculty successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateFaculty(Authentication authentication, @PathVariable Long id, @Valid @RequestBody UpdateFacultyDTO facultyDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        facultyService.updateFaculty(id, facultyDTO);
        return ResponseHelper.success("Update faculty successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFaculty(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        facultyService.deleteFaculty(id);
        return ResponseHelper.success("Delete faculty successfully", null);
    }
}
