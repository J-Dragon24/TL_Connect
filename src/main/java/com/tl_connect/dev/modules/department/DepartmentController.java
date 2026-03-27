package com.tl_connect.dev.modules.department;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.department.dto.DepartmentDTO;
import com.tl_connect.dev.modules.department.dto.CreateDepartmentDTO;
import com.tl_connect.dev.modules.department.dto.UpdateDepartmentDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllDepartments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<DepartmentDTO> response = departmentService.getAllDepartments(pageable);
        return ResponseHelper.success("Get all departments successfully", response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDepartment(@Valid @RequestBody CreateDepartmentDTO departmentDTO) {
        Long id = departmentService.createDepartment(departmentDTO);
        return ResponseHelper.success("Create department successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentDTO departmentDTO) {
        departmentService.updateDepartment(id, departmentDTO);
        return ResponseHelper.success("Update department successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseHelper.success("Delete department successfully", null);
    }
}
