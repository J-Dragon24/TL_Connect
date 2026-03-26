package com.tl_connect.dev.modules.faculty;

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
import com.tl_connect.dev.modules.faculty.dto.FacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.UpdateFacultyDTO;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final Validator validator;

    public PagedResponse<FacultyDTO> getAllFaculties(Pageable pageable) {
        Page<Faculty> faculties = facultyRepository.findAll(pageable);
        return new PagedResponse<>(
                faculties.getContent().stream().map(this::toDTO).toList(),
                faculties.getNumber(),
                faculties.getSize(),
                faculties.getTotalElements(),
                faculties.getTotalPages(),
                faculties.isFirst(),
                faculties.isLast());
    }

    @Transactional
    public Long createFaculty(FacultyDTO facultyDTO) {
        Set<ConstraintViolation<FacultyDTO>> violations = validator.validate(facultyDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        if (facultyRepository.existsByFacultyCode(facultyDTO.getFacultyCode())) {
            throw new ConflictException("Faculty code already exists");
        }

        Faculty faculty = new Faculty();
        faculty.setFacultyName(facultyDTO.getFacultyName());
        faculty.setFacultyCode(facultyDTO.getFacultyCode());
        faculty.setIsActive(true);
        try {
            facultyRepository.save(faculty);
            return faculty.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to create faculty");
        }
    }

    @Transactional
    public void updateFaculty(Long id, UpdateFacultyDTO facultyDTO) {
        Set<ConstraintViolation<UpdateFacultyDTO>> violations = validator.validate(facultyDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        if (facultyDTO.getFacultyCode() != null && !faculty.getFacultyCode().equals(facultyDTO.getFacultyCode()) && facultyRepository.existsByFacultyCode(facultyDTO.getFacultyCode())) {
            throw new ConflictException("Faculty code already exists");
        }

        if(facultyDTO.getFacultyName() != null){
            faculty.setFacultyName(facultyDTO.getFacultyName());
        }
        if(facultyDTO.getFacultyCode() != null){
            faculty.setFacultyCode(facultyDTO.getFacultyCode());
        }

        try {
            facultyRepository.save(faculty);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to update faculty");
        }
    }

    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found"));
        faculty.setIsActive(false);
        try {
            facultyRepository.save(faculty);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to delete faculty");
        }
    }

    private FacultyDTO toDTO(Faculty faculty) {
        return FacultyDTO.builder()
                .id(faculty.getId())
                .facultyCode(faculty.getFacultyCode())
                .facultyName(faculty.getFacultyName())
                .isActive(faculty.getIsActive())
                .build();
    }
    
}
