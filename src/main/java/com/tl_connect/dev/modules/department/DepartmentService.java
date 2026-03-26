package com.tl_connect.dev.modules.department;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.ConflictException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.department.dto.CreateDepartmentDTO;
import com.tl_connect.dev.modules.department.dto.DepartmentDTO;
import com.tl_connect.dev.modules.department.dto.UpdateDepartmentDTO;
import com.tl_connect.dev.modules.department.projection.DepartmentRow;
import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.FacultyRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final Validator validator;

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
        Set<ConstraintViolation<CreateDepartmentDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        if (departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new ConflictException("Department code already exists");
        }

        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        Department department = new Department();
        department.setDepartmentCode(dto.getDepartmentCode());
        department.setDepartmentName(dto.getDepartmentName());
        department.setFacultyId(faculty.getId());
        department.setIsActive(true);

        try {
            departmentRepository.save(department);
            return department.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to create department");
        }
    }

    @Transactional
    public void updateDepartment(Long id, UpdateDepartmentDTO dto) {
        Set<ConstraintViolation<UpdateDepartmentDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

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

        if (dto.getDepartmentName() != null) {
            department.setDepartmentName(dto.getDepartmentName());
        }

        if (dto.getDepartmentCode() != null) {
            department.setDepartmentCode(dto.getDepartmentCode());
        }

        try {
            departmentRepository.save(department);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to update department");
        }
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));

        department.setIsActive(false);

        try {
            departmentRepository.save(department);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to delete department");
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
