package com.tl_connect.dev.modules.major;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.ConflictException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.major.dto.CreateMajorDTO;
import com.tl_connect.dev.modules.major.dto.MajorAdmDTO;
import com.tl_connect.dev.modules.major.dto.UpdateMajorDTO;
import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.projection.MajorRow;
import com.tl_connect.dev.modules.major.repository.MajorRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;
    private final FacultyRepository facultyRepository;
    private final Validator validator;

    public PagedResponse<MajorAdmDTO> getAllMajors(Pageable pageable) {
        Page<MajorRow> majors = majorRepository.findAllMajors(pageable);
        return new PagedResponse<>(
                majors.getContent().stream().map(this::toDTO).toList(),
                majors.getNumber(),
                majors.getSize(),
                majors.getTotalElements(),
                majors.getTotalPages(),
                majors.isFirst(),
                majors.isLast());
    }

    @Transactional
    public Long createMajor(CreateMajorDTO majorDTO) {
        Set<ConstraintViolation<CreateMajorDTO>> violations = validator.validate(majorDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        if (majorRepository.existsByMajorCode(majorDTO.getMajorCode())) {
            throw new ConflictException("Major code already exists");
        }

        Faculty faculty = facultyRepository.findById(majorDTO.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        Major major = new Major();
            major.setMajorCode(majorDTO.getMajorCode());
            major.setMajorName(majorDTO.getMajorName());
            major.setFacultyId(faculty.getId());
            major.setIsActive(true);

        try {
            majorRepository.save(major);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to create major");
        }
        return major.getId();
    }


    @Transactional
    public void updateMajor(Long id, UpdateMajorDTO majorDTO) {
        Set<ConstraintViolation<UpdateMajorDTO>> violations = validator.validate(majorDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        Major major = majorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Major not found"));

        if (majorDTO.getMajorCode() != null && !major.getMajorCode().equals(majorDTO.getMajorCode()) && majorRepository.existsByMajorCode(majorDTO.getMajorCode())) {
            throw new ConflictException("Major code already exists");
        }

        if(majorDTO.getMajorCode() != null){
            major.setMajorCode(majorDTO.getMajorCode());
        }
        if(majorDTO.getMajorName() != null){
            major.setMajorName(majorDTO.getMajorName());
        }
        if(majorDTO.getFacultyId() != null){
            Faculty faculty = facultyRepository.findById(majorDTO.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));
            major.setFacultyId(faculty.getId());
        }

        try {
            majorRepository.save(major);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to update major");
        }
    }

    @Transactional
    public void deleteMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Major not found"));

        if (!major.getIsActive()) {
            throw new BadRequestException("Major already deleted");
        }
        major.setIsActive(false);
        try {
            majorRepository.save(major);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to delete major");
        }
    }

    private MajorAdmDTO toDTO(MajorRow major) {
        return MajorAdmDTO.builder()
                .id(major.getId())
                .majorName(major.getMajorName())
                .majorCode(major.getMajorCode())
                .facultyCode(major.getFacultyCode())
                .createdAt(major.getCreatedAt())
                .updatedAt(major.getUpdatedAt())
                .build();
    }
}
