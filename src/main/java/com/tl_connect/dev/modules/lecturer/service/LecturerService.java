package com.tl_connect.dev.modules.lecturer.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.enums.LecturerStatus;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.ConflictException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.lecturer.dto.CreateLecturerDTO;
import com.tl_connect.dev.modules.lecturer.dto.LecturerAdmInfoDTO;
import com.tl_connect.dev.modules.lecturer.dto.UpdateLecturerDTO;
import com.tl_connect.dev.modules.lecturer.entity.Lecturer;
import com.tl_connect.dev.modules.lecturer.projection.LecturerRow;
import com.tl_connect.dev.modules.lecturer.repository.LecturerRepository;
import com.tl_connect.dev.modules.department.DepartmentRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;

    public PagedResponse<LecturerAdmInfoDTO> getAllLecturers(Pageable pageable, String facultyCode) {
        if (facultyCode == null) {
            facultyCode = "";
        }
        Page<LecturerRow> lecturers = lecturerRepository.findAllLecturer(pageable, facultyCode);

        return new PagedResponse<>(
                        lecturers.getContent().stream().map(this::toFullInfo).toList(),
                        lecturers.getNumber(),
                        lecturers.getSize(),
                        lecturers.getTotalElements(),
                        lecturers.getTotalPages(),
                        lecturers.isFirst(),
                        lecturers.isLast());
    }

    public LecturerAdmInfoDTO getLecturerInfo(Long id) {
            LecturerRow lecturer = lecturerRepository.findLecturerById(id)
                    .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + id));
            return toFullInfo(lecturer);
    }

    @Transactional
    public Long createLecturer(CreateLecturerDTO lecturerDTO) {

        Set<ConstraintViolation<CreateLecturerDTO>> violations = validator.validate(lecturerDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        if (lecturerRepository.existsByLecturerCode(lecturerDTO.getLecturerCode())) {
            throw new ConflictException("Lecturer code already exists");
        }

        if (lecturerDTO.getDepartmentId() != null && !departmentRepository.existsById(lecturerDTO.getDepartmentId())) {
            throw new ConflictException("Department not found");
        }

        Lecturer lecturer = Lecturer.create(lecturerDTO.getLecturerCode(), lecturerDTO.getFullName(), lecturerDTO.getEmail(), lecturerDTO.getPhoneNumber(), lecturerDTO.getDepartmentId());

        try {
            lecturerRepository.save(lecturer);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to create lecturer: " + e.getMessage());
        }

        return lecturer.getId();
    }

    @Transactional
    public void updateLecturer(Long id, UpdateLecturerDTO lecturerDTO) {

        Set<ConstraintViolation<UpdateLecturerDTO>> violations = validator.validate(lecturerDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + id));

        if (lecturerDTO.getDepartmentId() != null && !departmentRepository.existsById(lecturerDTO.getDepartmentId())) {
            throw new ConflictException("Department not found");
        }

        if (lecturerDTO.getLecturerCode() != null && !lecturerDTO.getLecturerCode().equals(lecturer.getLecturerCode())) {
            if (lecturerRepository.existsByLecturerCode(lecturerDTO.getLecturerCode())) {
                throw new ConflictException("Lecturer code already exists");
            }
        }

        lecturer.update(lecturerDTO.getLecturerCode(), lecturerDTO.getFullName(), lecturerDTO.getEmail(), lecturerDTO.getPhoneNumber(), lecturerDTO.getDepartmentId());

        try {
            lecturerRepository.save(lecturer);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to update lecturer: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteLecturer(Long id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lecturer not found with id: " + id));

        if (lecturer.getStatus() == LecturerStatus.INACTIVE) {
            throw new BadRequestException("Lecturer is already inactive");
        }

        lecturer.deactivate();
        
        try {
            lecturerRepository.save(lecturer);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to delete lecturer: " + e.getMessage());
        }
    }


    
    private LecturerAdmInfoDTO toFullInfo(LecturerRow lecturer) {
        return LecturerAdmInfoDTO.builder()
                .id(lecturer.getId())
                .lecturerCode(lecturer.getLecturerCode())
                .fullName(lecturer.getFullName())
                .email(lecturer.getEmail())
                .phoneNumber(lecturer.getPhoneNumber())
                .departmentName(lecturer.getDepartmentName())
                .status(lecturer.getStatus())
                .build();
    }
}
