package com.tl_connect.dev.modules.department.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.department.dto.CreateDepartmentDTO;
import com.tl_connect.dev.modules.department.dto.DepartmentDTO;
import com.tl_connect.dev.modules.department.dto.UpdateDepartmentDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface DepartmentService {
    PagedResponse<DepartmentDTO> getAllDepartments(Pageable pageable);

    Long createDepartment(CreateDepartmentDTO dto);

    void updateDepartment(Long id, UpdateDepartmentDTO dto);
    
    void deleteDepartment(Long id);
}
