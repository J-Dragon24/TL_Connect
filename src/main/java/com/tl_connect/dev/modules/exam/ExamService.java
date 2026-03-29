package com.tl_connect.dev.modules.exam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
import com.tl_connect.dev.modules.exam.dto.CreateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDetailDTO;
import com.tl_connect.dev.modules.exam.dto.UpdateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.entity.ExamSchedule;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleAdminRow;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleView;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final SemesterRepository semesterRepository;
    private final FacultyRepository facultyRepository;
    private final Validator validator;

    public ExamScheduleDTO getExamSchedule(Long studentId, String semesterName) {
            Semester semester = semesterRepository.findSemesterByName(semesterName)
                            .orElseThrow(() -> new NotFoundException("Semester not found"));

            List<ExamScheduleView> examSchedules = examRepository.findExamSchedule(studentId, semester.getId());

            List<ExamScheduleDetailDTO> examScheduleDetails = examSchedules.stream()
                            .map(examSchedule -> ExamScheduleDetailDTO.builder()
                                            .subjectCode(examSchedule.getSubjectCode())
                                            .subjectName(examSchedule.getSubjectName())
                                            .classCode(examSchedule.getClassCode())
                                            .examDate(examSchedule.getExamDate())
                                            .startTime(examSchedule.getStartTime())
                                            .endTime(examSchedule.getEndTime())
                                            .examRoom(examSchedule.getExamRoom())
                                            .examLocation(examSchedule.getExamLocation())
                                            .examFormat(examSchedule.getExamFormat())
                                            .examType(examSchedule.getExamType())
                                            .examAttempt(examSchedule.getExamAttempt())
                                            .attendanceStatus(examSchedule.getAttendanceStatus())
                                            .build())
                            .toList();
            return ExamScheduleDTO.builder()
                            .semesterName(semester.getSemesterName())
                            .examSchedules(examScheduleDetails)
                            .build();
    }

    public PagedResponse<ExamScheduleBasicInfoDTO> getExamSchedule(Long semesterId, Pageable pageable, Long facultyId) {
            
            if(!semesterRepository.existsById(semesterId)){
                throw new NotFoundException("Semester not found");
            }

            Page<ExamScheduleAdminRow> page;
            if(facultyId != null){
                if(!facultyRepository.existsById(facultyId)){
                    throw new NotFoundException("Faculty not found");
                }
                page = examRepository.findAllExamScheduleByFacultyId(semesterId, facultyId, pageable);
            }else{
                page = examRepository.findAllExamSchedule(semesterId, pageable);
            }
            return new PagedResponse<>(
                    page.getContent().stream().map(this::toDTO).toList(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast());
    }

    @Transactional
    public void createExamSchedule(CreateExamScheduleDTO createExamScheduleDTO){
        Set<ConstraintViolation<CreateExamScheduleDTO>> violations = validator.validate(createExamScheduleDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        if (examRepository.existsConflict(
                createExamScheduleDTO.getExamDate(),
                createExamScheduleDTO.getSemesterId(),
                createExamScheduleDTO.getExamRoom(),
                createExamScheduleDTO.getStartTime(),
                createExamScheduleDTO.getEndTime()
        )) {
            throw new ConflictException("Exam schedule conflict");
        }

        ExamSchedule examSchedule = ExamSchedule.builder()
        .subjectId(createExamScheduleDTO.getSubjectId())
        .semesterId(createExamScheduleDTO.getSemesterId())
        .examDate(createExamScheduleDTO.getExamDate())
        .startTime(createExamScheduleDTO.getStartTime())
        .endTime(createExamScheduleDTO.getEndTime())
        .examRoom(createExamScheduleDTO.getExamRoom())
        .examLocation(createExamScheduleDTO.getExamLocation())
        .examFormat(createExamScheduleDTO.getExamFormat())
        .examType(createExamScheduleDTO.getExamType())
        .note(createExamScheduleDTO.getNote())
        .build();

        try{
            examRepository.save(examSchedule);
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Invalid exam schedule data: " + e.getMessage());
        }
    }

    @Transactional
    public void updateExamSchedule(Long id, UpdateExamScheduleDTO updateExamScheduleDTO){
        Set<ConstraintViolation<UpdateExamScheduleDTO>> violations = validator.validate(updateExamScheduleDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        
        ExamSchedule examSchedule = examRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Exam schedule not found"));

        Long semesterId = updateExamScheduleDTO.getSemesterId() != null ? updateExamScheduleDTO.getSemesterId() : examSchedule.getSemesterId();
        LocalDate examDate = updateExamScheduleDTO.getExamDate() != null ? updateExamScheduleDTO.getExamDate() : examSchedule.getExamDate();
        LocalTime startTime = updateExamScheduleDTO.getStartTime() != null ? updateExamScheduleDTO.getStartTime() : examSchedule.getStartTime();
        LocalTime endTime = updateExamScheduleDTO.getEndTime() != null ? updateExamScheduleDTO.getEndTime() : examSchedule.getEndTime();
        String examRoom = updateExamScheduleDTO.getExamRoom() != null ? updateExamScheduleDTO.getExamRoom() : examSchedule.getExamRoom();
       
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Start time must be before end time");
        }
        
        if(examRepository.existsConflictExcludingId(examDate, semesterId, examRoom, startTime, endTime, id)){
            throw new ConflictException("Exam schedule conflict");
        }

        Optional.ofNullable(updateExamScheduleDTO.getSubjectId()).ifPresent(examSchedule::setSubjectId);
        Optional.ofNullable(updateExamScheduleDTO.getSemesterId()).ifPresent(examSchedule::setSemesterId);
        Optional.ofNullable(updateExamScheduleDTO.getExamDate()).ifPresent(examSchedule::setExamDate);
        Optional.ofNullable(updateExamScheduleDTO.getStartTime()).ifPresent(examSchedule::setStartTime);
        Optional.ofNullable(updateExamScheduleDTO.getEndTime()).ifPresent(examSchedule::setEndTime);
        Optional.ofNullable(updateExamScheduleDTO.getExamRoom()).ifPresent(examSchedule::setExamRoom);
        Optional.ofNullable(updateExamScheduleDTO.getExamLocation()).ifPresent(examSchedule::setExamLocation);
        Optional.ofNullable(updateExamScheduleDTO.getExamFormat()).ifPresent(examSchedule::setExamFormat);
        Optional.ofNullable(updateExamScheduleDTO.getExamType()).ifPresent(examSchedule::setExamType);
        Optional.ofNullable(updateExamScheduleDTO.getNote()).ifPresent(examSchedule::setNote);
    }

    @Transactional
    public void deleteExamSchedule(Long id){
        ExamSchedule examSchedule = examRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Exam schedule not found"));
        try{
            examRepository.delete(examSchedule);
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Invalid exam schedule data: " + e.getMessage());
        }
    }
    

    private ExamScheduleBasicInfoDTO toDTO(ExamScheduleAdminRow examSchedule) {
            return ExamScheduleBasicInfoDTO.builder()
                            .id(examSchedule.getId())
                            .subjectCode(examSchedule.getSubjectCode())
                            .classCode(examSchedule.getClassCode())
                            .examDate(examSchedule.getExamDate())
                            .startTime(examSchedule.getStartTime())
                            .endTime(examSchedule.getEndTime())
                            .examRoom(examSchedule.getExamRoom())
                            .examLocation(examSchedule.getExamLocation())
                            .examFormat(examSchedule.getExamFormat())
                            .examType(examSchedule.getExamType())
                            .build();
    }
}
