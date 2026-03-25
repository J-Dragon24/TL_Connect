package com.tl_connect.dev.modules.lecturer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDTO;
import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDetailDTO;
import com.tl_connect.dev.modules.lecturer.dto.ClassBasicInfoDTO;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorDetailView;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorRow;
import com.tl_connect.dev.modules.lecturer.repository.AcademicAdvisorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicAdvisorService {
    private final AcademicAdvisorRepository academicAdvisorRepository;

    public PagedResponse<AcademicAdvisorDTO> getAll(Pageable pageable) {
        Page<AcademicAdvisorRow> lecturers = academicAdvisorRepository.findAllAcademicAdvisors(pageable);

        return new PagedResponse<>(
                        lecturers.getContent().stream().map(this::toDTO).toList(),
                        lecturers.getNumber(),
                        lecturers.getSize(),
                        lecturers.getTotalElements(),
                        lecturers.getTotalPages(),
                        lecturers.isFirst(),
                        lecturers.isLast());
    }

    public AcademicAdvisorDetailDTO getById(Long id) {
        AcademicAdvisorDetailView lecturer = academicAdvisorRepository.findAcademicAdvisorById(id)
                .orElseThrow(() -> new NotFoundException("Academic advisor not found with id: " + id));
        return AcademicAdvisorDetailDTO.builder()
                .id(lecturer.getId())
                .lecturerCode(lecturer.getLecturerCode())
                .lecturerName(lecturer.getLecturerName())
                .lecturerEmail(lecturer.getLecturerEmail())
                .lecturerPhoneNumber(lecturer.getLecturerPhoneNumber())
                .departmentCode(lecturer.getDepartmentCode())
                .lecturerStatus(lecturer.getLecturerStatus())
                .classInfo(ClassBasicInfoDTO.builder()
                        .classCode(lecturer.getStudentClassCode())
                        .majorCode(lecturer.getClassMajorCode())
                        .startYear(lecturer.getStudentClassYear())
                        .build())
                .build();
    }

    

    private AcademicAdvisorDTO toDTO(AcademicAdvisorRow row) {
        return AcademicAdvisorDTO.builder()
                .id(row.getId())
                .lecturerCode(row.getLecturerCode())
                .lecturerName(row.getLecturerName())
                .lecturerEmail(row.getLecturerEmail())
                .lecturerPhoneNumber(row.getLecturerPhoneNumber())
                .studentClassCode(row.getStudentClassCode())
                .build();
    }
}
