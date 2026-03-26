package com.tl_connect.dev.modules.semester;

import java.time.LocalDate;
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
    
    public List<Semester> getAllStudentSemesters(Long studentId) {
        YearStudyDTO yearStudy = studentInfoService.getYearStudy(studentId);
        
        List<Semester> semesters = semesterRepository.findAllStudentSemester(yearStudy.getStartYear(), yearStudy.getEndYear());
            return semesters;
    }

    public PagedResponse<Semester> getAll(Pageable pageable) {
        Page<Semester> semesters = semesterRepository.findAll(pageable);
        return new PagedResponse<>(
                semesters.getContent(),
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

        Semester semester = new Semester();
        semester.setAcademicYears(dto.getAcademicYears());
        semester.setSemesterName(dto.getSemesterName());
        semester.setSemesterCode(dto.getSemesterCode());
        semester.setSemesterNumber(dto.getSemesterNumber());
        semester.setStartDate(dto.getStartDate());
        semester.setEndDate(dto.getEndDate());
        semester.setIsActive(true);
        try {
            semesterRepository.save(semester);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to create semester");
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

        if (dto.getSemesterNumber() != null || dto.getAcademicYears() != null) {
            String academicYears = dto.getAcademicYears() != null ? dto.getAcademicYears() : semester.getAcademicYears();
            int semesterNumber = dto.getSemesterNumber() != null ? dto.getSemesterNumber() : semester.getSemesterNumber();
            if (semesterRepository.existsByAcademicYearsAndSemesterNumber(
                    academicYears, semesterNumber)) {
                throw new InvalidInputException("Semester already exists");
            }
            semester.setSemesterNumber(semesterNumber);
            semester.setAcademicYears(academicYears);
        }

        if(dto.getSemesterName() != null){
            semester.setSemesterName(dto.getSemesterName());
        }

        if(dto.getSemesterCode() != null){
            if (!dto.getSemesterCode().equals(semester.getSemesterCode()) && semesterRepository.existsBySemesterCode(dto.getSemesterCode())) {
                throw new InvalidInputException("Semester code " + dto.getSemesterCode() + " already exists");
            }
            semester.setSemesterCode(dto.getSemesterCode());
        }

        if(dto.getStartDate() != null || dto.getEndDate() != null){
            LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : semester.getStartDate();
            LocalDate endDate = dto.getEndDate() != null ? dto.getEndDate() : semester.getEndDate();
            if (startDate.isAfter(endDate)) {
                throw new InvalidInputException("Start date must be before end date");
            }
            semester.setStartDate(startDate);
            semester.setEndDate(endDate);
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
}