package com.tl_connect.dev.modules.semester;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.modules.semester.dto.CreateSemesterDTO;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.semester.dto.UpdateSemesterDTO;
import com.tl_connect.dev.modules.student.dto.YearStudyDTO;
import com.tl_connect.dev.modules.student.service.StudentService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


@Service
@RequiredArgsConstructor
public class SemesterService {
    
    private final SemesterRepository semesterRepository;
    private final StudentService studentInfoService;
    private final Validator validator;
    
    public List<SemesterDTO> getAllStudentSemesters(Long studentId) {
        YearStudyDTO yearStudy = studentInfoService.getYearStudy(studentId);
        
        List<Semester> semesters = semesterRepository.findAllStudentSemester(yearStudy.getStartYear(), yearStudy.getEndYear());
        return semesters.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PagedResponse<SemesterDTO> getAll(Pageable pageable) {
        Page<Semester> semesters = semesterRepository.findAll(pageable);
        return new PagedResponse<>(
                semesters.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
                semesters.getNumber(),
                semesters.getSize(),
                semesters.getTotalElements(),
                semesters.getTotalPages(),
                semesters.isFirst(),
                semesters.isLast());
    }

    @Transactional
    public Long createSemester(CreateSemesterDTO dto) {
        Set<ConstraintViolation<CreateSemesterDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        if (semesterRepository.existsByAcademicYearsAndSemesterNumber(dto.getAcademicYears(), dto.getSemesterNumber())) {
            throw new InvalidInputException("Semester " + dto.getAcademicYears() + "-" + dto.getSemesterNumber() + " already exists");
        }

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new InvalidInputException("Start date must be before end date");
        }

        if (semesterRepository.existsBySemesterCode(dto.getSemesterCode())) {
            throw new InvalidInputException("Semester code " + dto.getSemesterCode() + " already exists");
        }

        if(semesterRepository.violateDateRange(dto.getStartDate(), dto.getEndDate())){
            throw new InvalidInputException("Semester date range is overlap with another semester");
        }

        Semester semester = Semester.create(dto.getSemesterName(), dto.getSemesterCode(), dto.getAcademicYears(), dto.getSemesterNumber(), dto.getStartDate(), dto.getEndDate());
        try {
            semesterRepository.save(semester);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to create semester" + e.getMessage());
        }
        return semester.getId();
    }

    @Transactional
    public void updateSemester(Long id, UpdateSemesterDTO dto) {
        Set<ConstraintViolation<UpdateSemesterDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        Semester semester = semesterRepository.findById(id)
        .orElseThrow(() -> new InvalidInputException("Semester not found"));

        semester.update(dto.getSemesterName(), dto.getSemesterCode(), dto.getAcademicYears(), dto.getSemesterNumber(), dto.getStartDate(), dto.getEndDate());

        if (semesterRepository.existsByAcademicYearsAndSemesterNumberAndIdNot(semester.getAcademicYears(), semester.getSemesterNumber(), id)) {
            throw new InvalidInputException("Semester " + semester.getAcademicYears() + "-" + semester.getSemesterNumber() + " already exists");
        }

        if (semesterRepository.existsBySemesterCodeAndIdNot(semester.getSemesterCode(), id)) {
            throw new InvalidInputException("Semester code " + semester.getSemesterCode() + " already exists");
        }

        try {
            semesterRepository.save(semester);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to update semester");
        }
    }

    @Transactional
    public void deleteSemester(Long id) {
        Semester semester = semesterRepository.findById(id)
        .orElseThrow(() -> new InvalidInputException("Semester not found"));
        if(!semester.getIsActive()){
            throw new InvalidInputException("Semester is already deleted");
        }
        semester.setIsActive(false);
        try {
            semesterRepository.save(semester);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to delete semester");
        }
    }

    private SemesterDTO toDTO(Semester semester){
        return SemesterDTO.builder()
                .id(semester.getId())
                .semesterName(semester.getSemesterName())
                .semesterCode(semester.getSemesterCode())
                .academicYears(semester.getAcademicYears())
                .semesterNumber(semester.getSemesterNumber())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .isActive(semester.getIsActive())
                .build();
    }
}