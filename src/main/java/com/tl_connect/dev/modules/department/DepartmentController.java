package com.tl_connect.dev.modules.department;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.department.dto.DepartmentDTO;
import com.tl_connect.dev.modules.department.dto.CreateDepartmentDTO;
import com.tl_connect.dev.modules.department.dto.UpdateDepartmentDTO;
import com.tl_connect.dev.modules.department.service.interfaces.DepartmentService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllDepartments(Authentication authentication, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<DepartmentDTO> response = departmentService.getAllDepartments(pageable);
        return ResponseHelper.success("Get all departments successfully", response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDepartment(Authentication authentication, @Valid @RequestBody CreateDepartmentDTO departmentDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long id = departmentService.createDepartment(departmentDTO);
        return ResponseHelper.success("Create department successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateDepartment(Authentication authentication, @PathVariable Long id, @Valid @RequestBody UpdateDepartmentDTO departmentDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        departmentService.updateDepartment(id, departmentDTO);
        return ResponseHelper.success("Update department successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        departmentService.deleteDepartment(id);
        return ResponseHelper.success("Delete department successfully", null);
    }
}
