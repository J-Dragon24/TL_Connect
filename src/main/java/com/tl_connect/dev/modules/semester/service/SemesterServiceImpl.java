package com.tl_connect.dev.modules.semester.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.modules.semester.dto.CreateSemesterDTO;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.semester.dto.UpdateSemesterDTO;
import com.tl_connect.dev.modules.semester.service.interfaces.SemesterService;
import com.tl_connect.dev.modules.student.dto.YearStudyDTO;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {
    
    private final SemesterRepository semesterRepository;
    private final StudentService studentInfoService;
    
    public List<SemesterDTO> getAllStudentSemesters(Long studentId) {
        YearStudyDTO yearStudy = studentInfoService.getYearStudy(studentId);
        
        List<Semester> semesters = semesterRepository.findAllStudentSemester(yearStudy.getStartYear(), yearStudy.getEndYear());
        return semesters.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PagedResponse<SemesterDTO> getAll(Pageable pageable) {
        Page<Semester> semesters = semesterRepository.findAllSemesters(pageable);
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
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create semester");
        }
        return semester.getId();
    }

    @Transactional
    public void updateSemester(Long id, UpdateSemesterDTO dto) {
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
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update semester");
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
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete semester");
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

    public List<Semester> findBySemesterCodeIn(Set<String> semesterCodes) {
        return semesterRepository.findBySemesterCodeIn(semesterCodes);
    }

    public Semester findByIdAndIsActive(Long id){
        return semesterRepository.findByIdAndIsActiveTrue(id)
        .orElseThrow(() -> new InvalidInputException("Semester not found"));
    }

    @Override
    public boolean existsById(Long id){
        return semesterRepository.existsById(id);
    }

    public Semester findBySemesterCode(String semesterCode){
        return semesterRepository.findBySemesterCode(semesterCode)
            .orElseThrow(() -> new NotFoundException("Semester not found"));
    }

    public Semester findByDate(LocalDate date){
        return semesterRepository.findSemesterByDate(date)
            .orElseThrow(() -> new NotFoundException("Semester not found"));
    }
}