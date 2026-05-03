package com.tl_connect.dev.modules.lecturer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDTO;
import com.tl_connect.dev.modules.lecturer.dto.AcademicAdvisorDetailDTO;
import com.tl_connect.dev.modules.lecturer.dto.ClassBasicInfoDTO;
import com.tl_connect.dev.modules.lecturer.dto.CreateAcademicAdvisorDTO;
import com.tl_connect.dev.modules.lecturer.entity.AcademicAdvisor;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorDetailView;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorClassRow;
import com.tl_connect.dev.modules.lecturer.projection.LecturerRow;
import com.tl_connect.dev.modules.lecturer.repository.AcademicAdvisorRepository;
import com.tl_connect.dev.modules.lecturer.repository.LecturerRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicAdvisorService {
    private final AcademicAdvisorRepository academicAdvisorRepository;
    private final LecturerRepository lecturerRepository;

    public PagedResponse<AcademicAdvisorDTO> getAll(Pageable pageable) {
        Page<LecturerRow> lecturers = lecturerRepository.findAllLecturer(pageable, null);

        List<Long> lecturerIds = lecturers.getContent().stream().map(LecturerRow::getId).toList();

        List<AcademicAdvisorClassRow> academicAdvisors = academicAdvisorRepository.getClassByLecturerIds(lecturerIds);

        Map<Long, List<String>> academicAdvisorMap = academicAdvisors.stream()
                .collect(Collectors.groupingBy(
                        AcademicAdvisorClassRow::getLecturerId,
                        Collectors.mapping(AcademicAdvisorClassRow::getStudentClassCode, Collectors.toList())));

        List<AcademicAdvisorDTO> academicAdvisorDTOs = lecturers.getContent().stream()
                .map(row -> toDTO(row, academicAdvisorMap)).toList();
        return new PagedResponse<>(
                        academicAdvisorDTOs,
                        lecturers.getNumber(),
                        lecturers.getSize(),
                        lecturers.getTotalElements(),
                        lecturers.getTotalPages(),
                        lecturers.isFirst(),
                        lecturers.isLast());
    }

    public AcademicAdvisorDetailDTO getById(Long lecturerId) {
        List<AcademicAdvisorDetailView> lecturer = academicAdvisorRepository.findAcademicAdvisorByLecturerId(lecturerId);
        
        if(lecturer.isEmpty()) {
            throw new NotFoundException("Academic advisor not found with id: " + lecturerId);
        }
        
        return AcademicAdvisorDetailDTO.builder()
                .id(lecturer.get(0).getLecturerId())
                .lecturerCode(lecturer.get(0).getLecturerCode())
                .lecturerName(lecturer.get(0).getLecturerName())
                .lecturerEmail(lecturer.get(0).getLecturerEmail())
                .lecturerPhoneNumber(lecturer.get(0).getLecturerPhoneNumber())
                .departmentCode(lecturer.get(0).getDepartmentCode())
                .lecturerStatus(lecturer.get(0).getLecturerStatus())
                .classInfo(lecturer.stream().map(l -> ClassBasicInfoDTO.builder()
                        .academicAdvisorId(l.getId())
                        .classCode(l.getStudentClassCode())
                        .majorCode(l.getClassMajorCode())
                        .startYear(l.getStudentClassYear())
                        .build()).toList())
                .build();
    }

    @Transactional
    public Long create(CreateAcademicAdvisorDTO dto) {

        if(!lecturerRepository.existsById(dto.getLecturerId())) {
            throw new NotFoundException("Lecturer not found");
        }

        if(academicAdvisorRepository.existsByStudentClassId(dto.getStudentClassId())) {
            throw new ConflictException("Student class already has an academic advisor");
        }


        AcademicAdvisor academicAdvisor = AcademicAdvisor.create(dto.getLecturerId(), dto.getStudentClassId());
        try {
            academicAdvisorRepository.save(academicAdvisor);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to create academic advisor" + e.getMessage());
        }
        return academicAdvisor.getId();
    }

    @Transactional
    public void delete(Long studentClassId) {

        if(!academicAdvisorRepository.existsByStudentClassId(studentClassId)) {
            throw new NotFoundException("Academic advisor not found with student class id: " + studentClassId);
        }

        try {
            academicAdvisorRepository.deleteByStudentClassId(studentClassId);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to delete academic advisor");
        }
    }
    

    private AcademicAdvisorDTO toDTO(LecturerRow row, Map<Long, List<String>> academicAdvisorMap) {
        return AcademicAdvisorDTO.builder()
                .lecturerCode(row.getLecturerCode())
                .lecturerName(row.getFullName())
                .lecturerEmail(row.getEmail())
                .lecturerPhoneNumber(row.getPhoneNumber())
                .studentClassCodes(academicAdvisorMap.getOrDefault(row.getId(), new ArrayList<>()))
                .build();
    }
}
