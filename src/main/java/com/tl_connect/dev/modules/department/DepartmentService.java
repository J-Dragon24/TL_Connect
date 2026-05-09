package com.tl_connect.dev.modules.department;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.department.dto.CreateDepartmentDTO;
import com.tl_connect.dev.modules.department.dto.DepartmentDTO;
import com.tl_connect.dev.modules.department.dto.UpdateDepartmentDTO;
import com.tl_connect.dev.modules.department.projection.DepartmentRow;
import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    public PagedResponse<DepartmentDTO> getAllDepartments(Pageable pageable) {
        Page<DepartmentRow> departments = departmentRepository.findAllDepartment(pageable);
        return new PagedResponse<>(
                departments.getContent().stream().map(this::toDTO).toList(),
                departments.getNumber(),
                departments.getSize(),
                departments.getTotalElements(),
                departments.getTotalPages(),
                departments.isFirst(),
                departments.isLast());
    }

    @Transactional
    public Long createDepartment(CreateDepartmentDTO dto) {

        if (departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new ConflictException("Department code already exists");
        }

        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        Department department = Department.create(faculty.getId(), dto.getDepartmentCode(), dto.getDepartmentName());

        try {
            departmentRepository.save(department);
            return department.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create department");
        }
    }

    @Transactional
    public void updateDepartment(Long id, UpdateDepartmentDTO dto) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));

        if (dto.getDepartmentCode() != null
                && !department.getDepartmentCode().equals(dto.getDepartmentCode())
                && departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new ConflictException("Department code already exists");
        }

        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                    .orElseThrow(() -> new NotFoundException("Faculty not found"));
            department.setFacultyId(faculty.getId());
        }

        department.update(dto.getDepartmentCode(), dto.getDepartmentName());

        try {
            departmentRepository.save(department);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update department");
        }
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));

        department.deactivate();

        try {
            departmentRepository.save(department);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete department");
        }
    }

    private DepartmentDTO toDTO(DepartmentRow department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .facultyCode(department.getFacultyCode())
                .isActive(department.getIsActive())
                .build();
    }
}
