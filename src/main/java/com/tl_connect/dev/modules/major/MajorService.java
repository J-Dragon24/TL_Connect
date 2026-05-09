package com.tl_connect.dev.modules.major;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.major.dto.CreateMajorDTO;
import com.tl_connect.dev.modules.major.dto.MajorAdmDTO;
import com.tl_connect.dev.modules.major.dto.UpdateMajorDTO;
import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.projection.MajorRow;
import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;
    private final FacultyRepository facultyRepository;

    public PagedResponse<MajorAdmDTO> getAllMajors(Pageable pageable, String facultyCode) {
        if(facultyCode == null || facultyCode.isBlank()) {
            facultyCode = null;
        }
        Page<MajorRow> majors = majorRepository.findAllMajors(pageable, facultyCode);
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
        if (majorRepository.existsByMajorCode(majorDTO.getMajorCode())) {
            throw new ConflictException("Major code already exists");
        }

        Faculty faculty = facultyRepository.findById(majorDTO.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        Major major = Major.create(majorDTO.getMajorCode(), majorDTO.getMajorName(), faculty.getId());

        try {
            majorRepository.save(major);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create major");
        }
        return major.getId();
    }


    @Transactional
    public void updateMajor(Long id, UpdateMajorDTO majorDTO) {

        Major major = majorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Major not found"));

        if (majorDTO.getMajorCode() != null && !major.getMajorCode().equals(majorDTO.getMajorCode()) && majorRepository.existsByMajorCode(majorDTO.getMajorCode())) {
            throw new ConflictException("Major code already exists");
        }

        if(majorDTO.getFacultyId() != null){
            facultyRepository.findById(majorDTO.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));
        }

        major.update(majorDTO.getMajorCode(), majorDTO.getMajorName(), majorDTO.getFacultyId());

        try {
            majorRepository.save(major);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update major");
        }
    }

    @Transactional
    public void deleteMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Major not found"));

        if (!major.getIsActive()) {
            throw new ConflictException("Major already deleted");
        }
        major.delete();
        try {
            majorRepository.save(major);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete major");
        }
    }

    private MajorAdmDTO toDTO(MajorRow major) {
        return MajorAdmDTO.builder()
                .id(major.getId())
                .majorName(major.getMajorName())
                .majorCode(major.getMajorCode())
                .facultyCode(major.getFacultyCode())
                .isActive(major.getIsActive())
                .build();
    }
}
