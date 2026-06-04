package com.tl_connect.dev.modules.faculty.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.faculty.dto.CreateFacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.FacultyDTO;
import com.tl_connect.dev.modules.faculty.dto.UpdateFacultyDTO;
import com.tl_connect.dev.modules.faculty.service.interfaces.FacultyService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService{
    private final FacultyRepository facultyRepository;

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
    public Long createFaculty(CreateFacultyDTO facultyDTO) {

        if (facultyRepository.existsByFacultyCode(facultyDTO.getFacultyCode())) {
            throw new ConflictException("Faculty code already exists");
        }

        Faculty faculty = Faculty.create(facultyDTO.getFacultyCode(), facultyDTO.getFacultyName());
        try {
            facultyRepository.save(faculty);
            return faculty.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create faculty");
        }
    }

    @Transactional
    public void updateFaculty(Long id, UpdateFacultyDTO facultyDTO) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        if (facultyDTO.getFacultyCode() != null && !faculty.getFacultyCode().equals(facultyDTO.getFacultyCode()) && facultyRepository.existsByFacultyCode(facultyDTO.getFacultyCode())) {
            throw new ConflictException("Faculty code already exists");
        }

        faculty.update(facultyDTO.getFacultyCode(), facultyDTO.getFacultyName());

        try {
            facultyRepository.save(faculty);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update faculty");
        }
    }

    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found"));
        faculty.deactivate();
        try {
            facultyRepository.save(faculty);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete faculty");
        }
    }

    public Faculty findByIdAndIsActive(Long id){
        return facultyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found"));
    }

    public boolean existsById(Long id){
        return facultyRepository.existsById(id);
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
